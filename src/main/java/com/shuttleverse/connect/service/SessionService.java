package com.shuttleverse.connect.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

  private final Map<UUID, Set<String>> userSessions = new ConcurrentHashMap<>();

  private final Map<String, UUID> sessionToUser = new ConcurrentHashMap<>();

  public void registerSession(UUID userId, String sessionId) {
    userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    sessionToUser.put(sessionId, userId);
  }

  public void removeSession(String sessionId) {
    UUID userId = sessionToUser.remove(sessionId);
    if (userId != null) {
      Set<String> sessions = userSessions.get(userId);
      if (sessions != null) {
        sessions.remove(sessionId);
        if (sessions.isEmpty()) {
          userSessions.remove(userId);
        }
      }
    }
  }

  public Set<String> getUserSessions(UUID userId) {
    return userSessions.getOrDefault(userId, ConcurrentHashMap.newKeySet());
  }

  public boolean isUserOnline(UUID userId) {
    return userSessions.containsKey(userId) && !userSessions.get(userId).isEmpty();
  }

  public UUID getUserIdFromSession(String sessionId) {
    return sessionToUser.get(sessionId);
  }
}
