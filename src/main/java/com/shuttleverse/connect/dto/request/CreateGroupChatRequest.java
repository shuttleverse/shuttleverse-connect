package com.shuttleverse.connect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupChatRequest {
  @NotBlank(message = "Chat name is required")
  private String name;

  @NotEmpty(message = "At least one participant is required")
  private List<String> participantIds;
}

