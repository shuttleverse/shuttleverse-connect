package com.shuttleverse.connect.service;

import com.shuttleverse.connect.dto.response.MessageResponse;
import com.shuttleverse.connect.entity.Chat;
import com.shuttleverse.connect.entity.Message;
import com.shuttleverse.connect.enums.MessageStatus;
import com.shuttleverse.connect.exception.ChatNotFoundException;
import com.shuttleverse.connect.exception.UnauthorizedChatAccessException;
import com.shuttleverse.connect.repository.ChatParticipantRepository;
import com.shuttleverse.connect.repository.ChatRepository;
import com.shuttleverse.connect.repository.MessageRepository;
import com.shuttleverse.connect.util.UserIdConverter;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final ChatParticipantRepository chatParticipantRepository;
  private final ChatService chatService;
  private final MessageMapper messageMapper;
  private final SessionService sessionService;
  private final SimpMessagingTemplate messagingTemplate;

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
    // Broadcast to all subscribers of the chat topic
    messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);

    // Also send personal notifications to online participants (excluding sender)
    List<UUID> participants = chatService.getChatParticipants(chatId);
    UUID senderUuid = message.getSenderId() != null ? java.util.UUID.fromString(message.getSenderId()) : null;

    for (UUID participantId : participants) {
      if (!participantId.equals(senderUuid) && sessionService.isUserOnline(participantId)) {
        // Note: convertAndSendToUser uses Principal name, which is OAuth subject ID
        // For now, we rely on topic subscription for message delivery
        // Personal notifications can be added later if needed
      }
    }
  }

}
