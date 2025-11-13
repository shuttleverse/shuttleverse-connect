package com.shuttleverse.connect.service;

import com.shuttleverse.connect.client.UserServiceClient;
import com.shuttleverse.connect.dto.NotificationPayload;
import com.shuttleverse.connect.dto.external.SVApiResponse;
import com.shuttleverse.connect.dto.external.SVUser;
import com.shuttleverse.connect.dto.response.MessageResponse;
import com.shuttleverse.connect.entity.Chat;
import com.shuttleverse.connect.entity.Message;
import com.shuttleverse.connect.enums.MessageStatus;
import com.shuttleverse.connect.exception.ChatNotFoundException;
import com.shuttleverse.connect.exception.UnauthorizedChatAccessException;
import com.shuttleverse.connect.repository.ChatParticipantRepository;
import com.shuttleverse.connect.repository.ChatRepository;
import com.shuttleverse.connect.repository.MessageRepository;
import com.shuttleverse.connect.util.PushNotificationUtils;
import com.shuttleverse.connect.util.UserIdConverter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final ChatParticipantRepository chatParticipantRepository;
  private final ChatService chatService;
  private final MessageMapper messageMapper;
  private final SessionService sessionService;
  private final SimpMessagingTemplate messagingTemplate;
  private final PushNotificationService pushNotificationService;
  private final UserServiceClient userServiceClient;

  @Transactional
  public MessageResponse sendMessage(UUID chatId, String senderId, String content) {
    // Convert OAuth subject ID to UUID before storing
    UUID senderUuid = UserIdConverter.toUuid(senderId);

    Chat chat = chatRepository.findById(chatId)
        .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

    chatService.validateUserInChat(chatId, senderUuid);

    Message message = Message.builder()
        .chat(chat)
        .senderId(senderUuid)
        .content(content)
        .status(MessageStatus.SENT)
        .build();

    message = messageRepository.save(message);

    // Update chat's updatedAt timestamp so it appears at the top of chat list
    chat.setUpdatedAt(java.time.LocalDateTime.now());
    chatRepository.save(chat);

    MessageResponse response = messageMapper.convertToMessageResponse(message);

    broadcastMessage(response, chatId);

    return response;
  }

  @Transactional(readOnly = true)
  public Page<MessageResponse> getChatMessages(UUID chatId, String userId, Pageable pageable) {
    UUID userUuid = UserIdConverter.toUuid(userId);
    chatService.validateUserInChat(chatId, userUuid);

    Page<Message> messages = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId, pageable);

    return messages.map(messageMapper::convertToMessageResponse);
  }

  @Transactional
  public void markMessagesAsRead(UUID chatId, String userId) {
    UUID userUuid = UserIdConverter.toUuid(userId);
    chatService.validateUserInChat(chatId, userUuid);

    var participantOpt = chatParticipantRepository.findByChatIdAndUserId(chatId, userUuid);
    if (participantOpt.isEmpty()) {
      throw new UnauthorizedChatAccessException("User is not a participant in this chat");
    }

    var participant = participantOpt.get();
    var now = java.time.LocalDateTime.now();
    participant.setLastReadAt(now);
    chatParticipantRepository.save(participant);

    var unreadMessages = messageRepository.findUnreadMessagesByChatIdAndUserId(chatId, userUuid,
            org.springframework.data.domain.PageRequest.of(0, 100))
        .getContent();

    for (Message message : unreadMessages) {
      if (message.getReadAt() == null) {
        message.setStatus(MessageStatus.READ);
        message.setReadAt(now);
      }
    }

    messageRepository.saveAll(unreadMessages);
  }

  @Transactional(readOnly = true)
  public long getUnreadCount(UUID chatId, String userId) {
    UUID userUuid = UserIdConverter.toUuid(userId);
    return messageRepository.countUnreadMessages(chatId, userUuid);
  }

  public void broadcastMessage(MessageResponse message, UUID chatId) {
    messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);

    List<UUID> participants = chatService.getChatParticipants(chatId);
    UUID senderUuid = message.getSenderId() != null
        ? UUID.fromString(message.getSenderId())
        : null;

    String senderName = null;
    try {
      if (senderUuid != null) {
        SVApiResponse<SVUser> userResponse = userServiceClient.getUserById(senderUuid);
        if (userResponse != null && userResponse.isSuccess() && userResponse.getData() != null) {
          senderName = userResponse.getData().getUsername();
        }
      }
    } catch (Exception e) {
      log.warn("Failed to get sender info for push notification", e);
    }

    if (senderName == null) {
      return;
    }

    // Send push notifications to offline users
    for (UUID participantId : participants) {
      if (participantId.equals(senderUuid)) {
        continue; // Skip sender of message
      }

      boolean isOnline = sessionService.isUserOnline(participantId);

      // Send notification if user is offline
      if (!isOnline) {
        sendPushNotificationForMessage(participantId, message, chatId, senderName);
      }
    }
  }

  private void sendPushNotificationForMessage(UUID userId, MessageResponse message, UUID chatId,
      String senderName) {
    try {
      String messagePreview = PushNotificationUtils.truncateMessage(message.getContent(), 100);

      NotificationPayload payload = NotificationPayload.builder()
          .title("New message from " + senderName)
          .body(messagePreview)
          .icon("/logo-temp.png")
          .tag(chatId.toString())
          .requireInteraction(false)
          .data(Map.of(
              "chatId", chatId.toString(),
              "messageId", message.getId().toString(),
              "type", "message"
          ))
          .build();

      pushNotificationService.sendPushNotificationToUser(userId, payload);
    } catch (Exception e) {
      log.error("Failed to send push notification for message", e);
    }
  }

}
