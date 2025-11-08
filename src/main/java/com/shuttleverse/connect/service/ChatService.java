package com.shuttleverse.connect.service;

import com.shuttleverse.connect.client.UserServiceClient;
import com.shuttleverse.connect.dto.external.SVApiResponse;
import com.shuttleverse.connect.dto.external.SVUser;
import com.shuttleverse.connect.dto.external.UserDto;
import com.shuttleverse.connect.dto.request.AddParticipantRequest;
import com.shuttleverse.connect.dto.request.CreateDirectChatRequest;
import com.shuttleverse.connect.dto.request.CreateGroupChatRequest;
import com.shuttleverse.connect.dto.response.ChatResponse;
import com.shuttleverse.connect.dto.response.MessageResponse;
import com.shuttleverse.connect.entity.Chat;
import com.shuttleverse.connect.entity.ChatParticipant;
import com.shuttleverse.connect.enums.ChatType;
import com.shuttleverse.connect.enums.ParticipantRole;
import com.shuttleverse.connect.exception.ChatNotFoundException;
import com.shuttleverse.connect.exception.UnauthorizedChatAccessException;
import com.shuttleverse.connect.exception.UserNotInChatException;
import com.shuttleverse.connect.repository.ChatParticipantRepository;
import com.shuttleverse.connect.repository.ChatRepository;
import com.shuttleverse.connect.repository.MessageRepository;
import com.shuttleverse.connect.util.UserIdConverter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

  private final ChatRepository chatRepository;
  private final ChatParticipantRepository chatParticipantRepository;
  private final MessageRepository messageRepository;
  private final UserServiceClient userServiceClient;
  private final MessageMapper messageMapper;
  private final UserMapper userMapper;

  @Transactional
  public ChatResponse createDirectChat(String creatorId, CreateDirectChatRequest request) {
    UUID creatorUuid = UserIdConverter.toUuid(creatorId);
    UUID targetUserUuid = UserIdConverter.toUuid(request.getTargetUserId());

    // Check if direct chat already exists
    Chat existingChat = chatRepository.findDirectChatBetweenUsers(
        ChatType.DIRECT, creatorUuid, targetUserUuid)
        .orElse(null);

    if (existingChat != null) {
      return convertToChatResponse(existingChat, creatorUuid);
    }

    Chat newChat = Chat.builder()
        .type(ChatType.DIRECT)
        .createdBy(creatorUuid)
        .build();

    Chat savedChat = chatRepository.save(newChat);

    ChatParticipant participant1 = ChatParticipant.builder()
        .chat(savedChat)
        .userId(creatorUuid)
        .role(ParticipantRole.MEMBER)
        .build();

    ChatParticipant participant2 = ChatParticipant.builder()
        .chat(savedChat)
        .userId(targetUserUuid)
        .role(ParticipantRole.MEMBER)
        .build();

    chatParticipantRepository.saveAll(List.of(participant1, participant2));

    return convertToChatResponse(savedChat, creatorUuid);
  }

  @Transactional
  public ChatResponse createGroupChat(String creatorId, CreateGroupChatRequest request) {
    // Convert OAuth subject ID to UUID before storing
    UUID creatorUuid = UserIdConverter.toUuid(creatorId);

    Chat newChat = Chat.builder()
        .type(ChatType.GROUP)
        .name(request.getName())
        .createdBy(creatorUuid)
        .build();

    Chat savedChat = chatRepository.save(newChat);

    ChatParticipant owner = ChatParticipant.builder()
        .chat(savedChat)
        .userId(creatorUuid)
        .role(ParticipantRole.OWNER)
        .build();

    chatParticipantRepository.save(owner);

    List<ChatParticipant> participants = request.getParticipantIds().stream()
        .map(UserIdConverter::toUuid)
        .filter(uuid -> uuid != null && !uuid.equals(creatorUuid))
        .map(userUuid -> ChatParticipant.builder()
            .chat(savedChat)
            .userId(userUuid)
            .role(ParticipantRole.MEMBER)
            .build())
        .collect(Collectors.toList());

    chatParticipantRepository.saveAll(participants);

    return convertToChatResponse(savedChat, creatorUuid);
  }

  @Transactional(readOnly = true)
  public List<ChatResponse> getUserChats(String userId) {
    UUID userUuid = UserIdConverter.toUuid(userId);
    List<Chat> chats = chatRepository.findByParticipantUserId(userUuid);
    return chats.stream()
        .map(chat -> convertToChatResponse(chat, userUuid))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public ChatResponse getChatById(UUID chatId, String userId) {
    UUID userUuid = UserIdConverter.toUuid(userId);
    Chat chat = chatRepository.findById(chatId)
        .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

    validateUserInChat(chatId, userUuid);

    return convertToChatResponse(chat, userUuid);
  }

  @Transactional
  public void addParticipant(UUID chatId, String requesterId, AddParticipantRequest request) {
    UUID requesterUuid = UserIdConverter.toUuid(requesterId);
    UUID participantUuid = UserIdConverter.toUuid(request.getUserId());

    Chat chat = chatRepository.findById(chatId)
        .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

    if (chat.getType() != ChatType.GROUP) {
      throw new UnauthorizedChatAccessException("Cannot add participants to direct chat");
    }

    validateUserInChat(chatId, requesterUuid);

    if (chatParticipantRepository.existsByChatIdAndUserId(chatId, participantUuid)) {
      return;
    }

    ChatParticipant participant = ChatParticipant.builder()
        .chat(chat)
        .userId(participantUuid)
        .role(ParticipantRole.MEMBER)
        .build();

    chatParticipantRepository.save(participant);
  }

  @Transactional
  public void removeParticipant(UUID chatId, String requesterId, String participantToRemove) {
    UUID requesterUuid = UserIdConverter.toUuid(requesterId);
    UUID participantUuid = UserIdConverter.toUuid(participantToRemove);

    if (!chatRepository.existsById(chatId)) {
      throw new ChatNotFoundException("Chat not found with id: " + chatId);
    }

    validateUserInChat(chatId, requesterUuid);

    ChatParticipant participant = chatParticipantRepository
        .findByChatIdAndUserId(chatId, participantUuid)
        .orElseThrow(() -> new UserNotInChatException("User is not a participant in this chat"));

    chatParticipantRepository.delete(participant);
  }

  @Transactional(readOnly = true)
  public void validateUserInChat(UUID chatId, UUID userId) {
    if (!chatParticipantRepository.existsByChatIdAndUserId(chatId, userId)) {
      throw new UnauthorizedChatAccessException("User is not a participant in this chat");
    }
  }

  @Transactional(readOnly = true)
  public List<UUID> getChatParticipants(UUID chatId) {
    return chatParticipantRepository.findUserIdsByChatId(chatId);
  }

  private ChatResponse convertToChatResponse(Chat chat, UUID requestingUserId) {
    List<UUID> participantUuids = chatParticipantRepository.findUserIdsByChatId(chat.getId());

    SVApiResponse<List<SVUser>> response = null;
    if (!participantUuids.isEmpty()) {
      response = userServiceClient.getUsersByIds(participantUuids);
    }

    List<UserDto> participants = List.of();
    if (response != null && response.isSuccess() && response.getData() != null) {
      participants = response.getData().stream()
          .map(userMapper::toUserDto)
          .collect(java.util.stream.Collectors.toList());
    }

    MessageResponse lastMessage = null;
    var lastMessageOpt = messageRepository.findByChatIdOrderByCreatedAtDesc(
        chat.getId(), org.springframework.data.domain.PageRequest.of(0, 1))
        .stream()
        .findFirst();

    if (lastMessageOpt.isPresent()) {
      lastMessage = messageMapper.convertToMessageResponse(lastMessageOpt.get());
    }

    long unreadCount = messageRepository.countUnreadMessages(chat.getId(), requestingUserId);

    return ChatResponse.builder()
        .id(chat.getId())
        .type(chat.getType())
        .name(chat.getName())
        .participants(participants)
        .lastMessage(lastMessage)
        .unreadCount(unreadCount)
        .createdAt(chat.getCreatedAt())
        .updatedAt(chat.getUpdatedAt())
        .build();
  }
}
