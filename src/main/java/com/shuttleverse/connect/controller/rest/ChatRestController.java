package com.shuttleverse.connect.controller.rest;

import com.shuttleverse.connect.dto.external.SVApiResponse;
import com.shuttleverse.connect.dto.request.AddParticipantRequest;
import com.shuttleverse.connect.dto.request.CreateDirectChatRequest;
import com.shuttleverse.connect.dto.request.CreateGroupChatRequest;
import com.shuttleverse.connect.dto.response.ChatListResponse;
import com.shuttleverse.connect.dto.response.ChatResponse;
import com.shuttleverse.connect.service.ChatService;
import com.shuttleverse.connect.util.UserContext;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chats")
public class ChatRestController {

  private final ChatService chatService;

  public ChatRestController(ChatService chatService) {
    this.chatService = chatService;
  }

  @PostMapping("/direct")
  public ResponseEntity<SVApiResponse<ChatResponse>> createDirectChat(
      @Valid @RequestBody CreateDirectChatRequest request) {
    String userId = UserContext.getCurrentUserId();
    ChatResponse chat = chatService.createDirectChat(userId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(SVApiResponse.success(chat));
  }

  @PostMapping("/group")
  public ResponseEntity<SVApiResponse<ChatResponse>> createGroupChat(
      @Valid @RequestBody CreateGroupChatRequest request) {
    String userId = UserContext.getCurrentUserId();
    ChatResponse chat = chatService.createGroupChat(userId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(SVApiResponse.success(chat));
  }

  @GetMapping
  public ResponseEntity<SVApiResponse<ChatListResponse>> getUserChats() {
    String userId = UserContext.getCurrentUserId();
    List<ChatResponse> chats = chatService.getUserChats(userId);
    ChatListResponse response = new ChatListResponse();
    response.setChats(chats);
    return ResponseEntity.ok(SVApiResponse.success(response));
  }

  @GetMapping("/{chatId}")
  public ResponseEntity<SVApiResponse<ChatResponse>> getChatById(@PathVariable UUID chatId) {
    String userId = UserContext.getCurrentUserId();
    ChatResponse chat = chatService.getChatById(chatId, userId);
    return ResponseEntity.ok(SVApiResponse.success(chat));
  }

  @PostMapping("/{chatId}/participants")
  public ResponseEntity<SVApiResponse<Void>> addParticipant(
      @PathVariable UUID chatId,
      @Valid @RequestBody AddParticipantRequest request) {
    String userId = UserContext.getCurrentUserId();
    chatService.addParticipant(chatId, userId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(SVApiResponse.success("Participant added successfully", null));
  }

  @DeleteMapping("/{chatId}/participants/{participantId}")
  public ResponseEntity<SVApiResponse<Void>> removeParticipant(
      @PathVariable UUID chatId,
      @PathVariable String participantId) {
    String userId = UserContext.getCurrentUserId();
    chatService.removeParticipant(chatId, userId, participantId);
    return ResponseEntity.ok(SVApiResponse.success("Participant removed successfully", null));
  }
}

