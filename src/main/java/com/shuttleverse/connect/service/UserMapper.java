package com.shuttleverse.connect.service;

import com.shuttleverse.connect.dto.external.SVUser;
import com.shuttleverse.connect.dto.external.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(SVUser svUser) {
        if (svUser == null) {
            return null;
        }

        return UserDto.builder()
                .id(svUser.getId() != null ? svUser.getId().toString() : null)
                .username(svUser.getUsername())
                .email(svUser.getEmail())
                .firstName(null) // SVUser doesn't have firstName
                .lastName(null) // SVUser doesn't have lastName
                .avatarUrl(null) // SVUser doesn't have avatarUrl
                .build();
    }
}
