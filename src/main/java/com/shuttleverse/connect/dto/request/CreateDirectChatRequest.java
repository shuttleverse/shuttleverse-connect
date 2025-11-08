package com.shuttleverse.connect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDirectChatRequest {
  @NotBlank(message = "Target user ID is required")
  private String targetUserId;
}

