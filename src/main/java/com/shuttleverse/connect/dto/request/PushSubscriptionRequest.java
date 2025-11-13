package com.shuttleverse.connect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscriptionRequest {
    
    @NotBlank(message = "Endpoint is required")
    private String endpoint;
    
    @NotNull(message = "Keys are required")
    private Keys keys;
    
    private String userAgent;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Keys {
        @NotBlank(message = "p256dh key is required")
        private String p256dh;
        
        @NotBlank(message = "auth key is required")
        private String auth;
    }
}

