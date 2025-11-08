package com.shuttleverse.connect.controller.rest;

import com.shuttleverse.connect.dto.external.SVApiResponse;
import com.shuttleverse.connect.dto.request.SendMessageRequest;
import com.shuttleverse.connect.dto.response.MessageResponse;
import com.shuttleverse.connect.service.MessageService;
import com.shuttleverse.connect.util.UserContext;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chats/{chatId}/messages")
public class MessageRestController {

  private final MessageService messageService;

  public MessageRestController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping
  public ResponseEntity<SVApiResponse<MessageResponse>> sendMessage(
      @PathVariable UUID chatId,
      @Valid @RequestBody SendMessageRequest request) {
    String userId = UserContext.getCurrentUserId();
    MessageResponse message = messageService.sendMessage(chatId, userId, request.getContent());
    return ResponseEntity.ok(SVApiResponse.success(message));
  }

  @GetMapping
  public ResponseEntity<SVApiResponse<Page<MessageResponse>>> getChatMessages(
      @PathVariable UUID chatId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    String userId = UserContext.getCurrentUserId();
    Pageable pageable = PageRequest.of(page, size);
    Page<MessageResponse> messages = messageService.getChatMessages(chatId, userId, pageable);
    return ResponseEntity.ok(SVApiResponse.success(messages));
  }

  @PostMapping("/read")
  public ResponseEntity<SVApiResponse<Void>> markMessagesAsRead(@PathVariable UUID chatId) {
    String userId = UserContext.getCurrentUserId();
    messageService.markMessagesAsRead(chatId, userId);
    return ResponseEntity.ok(SVApiResponse.success("Messages marked as read", null));
  }

  @GetMapping("/unread")
  public ResponseEntity<SVApiResponse<Long>> getUnreadCount(@PathVariable UUID chatId) {
    String userId = UserContext.getCurrentUserId();
    long unreadCount = messageService.getUnreadCount(chatId, userId);
    return ResponseEntity.ok(SVApiResponse.success(unreadCount));
  }
}

