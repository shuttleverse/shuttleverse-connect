package com.shuttleverse.connect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscriptionResponse {
    private UUID id;
    private UUID userId;
    private String endpoint;
    private String deviceType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

