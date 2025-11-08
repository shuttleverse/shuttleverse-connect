package com.shuttleverse.connect.dto.response;

import com.shuttleverse.connect.dto.external.UserDto;
import com.shuttleverse.connect.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
  private UUID id;
  private ChatType type;
  private String name;
  private List<UserDto> participants;
  private MessageResponse lastMessage;
  private Long unreadCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}

