package com.shuttleverse.connect.config;

import com.shuttleverse.connect.service.SessionService;
import com.shuttleverse.connect.util.UserIdConverter;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
  private final SessionService sessionService;

  public WebSocketEventListener(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();

    Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
    if (sessionAttributes != null) {
      String oauthUserId = (String) sessionAttributes.get("userId");
      if (oauthUserId != null) {
        UUID userId = UserIdConverter.toUuid(oauthUserId);
        if (userId != null) {
          sessionService.registerSession(userId, sessionId);
          logger.info("User connected: {} (UUID: {}) with session: {}", oauthUserId, userId,
              sessionId);
        }
      }
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();

    UUID userId = sessionService.getUserIdFromSession(sessionId);
    if (userId != null) {
      sessionService.removeSession(sessionId);
      logger.info("User disconnected: {} with session: {}", userId, sessionId);
    }
  }
}

