package com.shuttleverse.connect.util;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility to convert OAuth subject IDs to UUIDs using the same method
 * as the community service: UUID.nameUUIDFromBytes(sub.getBytes())
 * Also handles UUID strings by parsing them directly.
 */
public class UserIdConverter {

    /**
     * Converts a user ID string to a UUID.
     * If the string is already a valid UUID, it's parsed directly.
     * Otherwise, it's treated as an OAuth subject ID and converted using
     * the same deterministic method as the community service.
     */
    public static UUID toUuid(String userId) {
        if (userId == null) {
            return null;
        }
        
        // Try to parse as UUID first (if it's already a UUID string)
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            // Not a valid UUID string, treat as OAuth subject ID
            return UUID.nameUUIDFromBytes(userId.getBytes());
        }
    }

    /**
     * Converts a list of user IDs to UUIDs.
     */
    public static List<UUID> toUuids(List<String> userIds) {
        if (userIds == null) {
            return null;
        }
        return userIds.stream()
                .map(UserIdConverter::toUuid)
                .collect(Collectors.toList());
    }
}

