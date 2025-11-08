package com.shuttleverse.connect.controller;

import com.shuttleverse.connect.dto.request.SendMessageRequest;
import com.shuttleverse.connect.service.ChatService;
import com.shuttleverse.connect.service.MessageService;
import com.shuttleverse.connect.util.FeignAuthContext;
import com.shuttleverse.connect.util.UserIdConverter;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  private final MessageService messageService;
  private final ChatService chatService;

  public ChatController(
      MessageService messageService,
      ChatService chatService) {
    this.messageService = messageService;
    this.chatService = chatService;
  }

  @MessageMapping("/chat/{chatId}/send")
  public void sendMessage(
      @DestinationVariable UUID chatId,
      @Payload SendMessageRequest request,
      SimpMessageHeaderAccessor headerAccessor) {

    String userId = getUserIdFromHeaderAccessor(headerAccessor);
    if (userId == null) {
      return;
    }

    setTokenInContext(headerAccessor);

    try {
      messageService.sendMessage(chatId, userId, request.getContent());
    } finally {
      FeignAuthContext.clear();
    }
  }

  @MessageMapping("/chat/{chatId}/typing")
  public void sendTypingIndicator(
      @DestinationVariable UUID chatId,
      SimpMessageHeaderAccessor headerAccessor) {

    String userId = getUserIdFromHeaderAccessor(headerAccessor);
    if (userId == null) {
      return;
    }

    setTokenInContext(headerAccessor);

    try {
      UUID userUuid = UserIdConverter.toUuid(userId);
      if (userUuid != null) {
        chatService.validateUserInChat(chatId, userUuid);
      }
    } finally {
      FeignAuthContext.clear();
    }
  }

  private String getUserIdFromHeaderAccessor(SimpMessageHeaderAccessor headerAccessor) {
    if (headerAccessor.getUser() != null) {
      return headerAccessor.getUser().getName();
    }

    Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    if (sessionAttributes != null) {
      String userId = (String) sessionAttributes.get("userId");
      if (userId != null) {
        return userId;
      }
    }

    return null;
  }

  private void setTokenInContext(SimpMessageHeaderAccessor headerAccessor) {
    Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    if (sessionAttributes != null) {
      String token = (String) sessionAttributes.get("token");
      if (token != null) {
        FeignAuthContext.setToken(token);
      }
    }
  }
}