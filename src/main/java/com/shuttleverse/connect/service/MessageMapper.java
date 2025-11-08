package com.shuttleverse.connect.service;

import com.shuttleverse.connect.client.UserServiceClient;
import com.shuttleverse.connect.dto.external.SVApiResponse;
import com.shuttleverse.connect.dto.external.UserDto;
import com.shuttleverse.connect.dto.response.MessageResponse;
import com.shuttleverse.connect.entity.Message;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;

    public MessageResponse convertToMessageResponse(Message message) {
        // senderId is already stored as UUID in database
        UUID senderUuid = message.getSenderId();

        SVApiResponse<com.shuttleverse.connect.dto.external.SVUser> response = null;
        if (senderUuid != null) {
            response = userServiceClient.getUserById(senderUuid);
        }

        UserDto sender = null;
        if (response != null && response.isSuccess() && response.getData() != null) {
            sender = userMapper.toUserDto(response.getData());
        }

        return MessageResponse.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(senderUuid != null ? senderUuid.toString() : null)
                .sender(sender)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .editedAt(message.getEditedAt())
                .readAt(message.getReadAt())
                .status(message.getStatus())
                .isEdited(message.getEditedAt() != null)
                .isDeleted(message.getDeletedAt() != null)
                .build();
    }
}
