package com.shuttleverse.connect.dto.response;

import com.shuttleverse.connect.dto.external.UserDto;
import com.shuttleverse.connect.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
  private UUID id;
  private UUID chatId;
  private String senderId;
  private UserDto sender;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime editedAt;
  private LocalDateTime readAt;
  private MessageStatus status;
  private Boolean isEdited;
  private Boolean isDeleted;
}

