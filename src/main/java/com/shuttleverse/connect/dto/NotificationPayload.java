package com.shuttleverse.connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPayload {
    private String title;
    private String body;
    private String icon;
    private String badge;
    private String tag;
    private boolean requireInteraction;
    private Map<String, Object> data;
    
    public Map<String, Object> getData() {
        if (data == null) {
            data = new HashMap<>();
        }
        return data;
    }
}

