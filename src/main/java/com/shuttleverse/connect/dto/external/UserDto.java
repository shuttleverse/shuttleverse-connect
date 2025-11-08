package com.shuttleverse.connect.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private String id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String avatarUrl;
}

