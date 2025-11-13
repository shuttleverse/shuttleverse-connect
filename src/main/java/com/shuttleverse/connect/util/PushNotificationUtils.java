package com.shuttleverse.connect.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuttleverse.connect.dto.NotificationPayload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PushNotificationUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static String toJson(NotificationPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Error converting notification payload to JSON", e);
            throw new RuntimeException("Failed to serialize notification payload", e);
        }
    }
    
    public static String truncateMessage(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength - 3) + "...";
    }
}

