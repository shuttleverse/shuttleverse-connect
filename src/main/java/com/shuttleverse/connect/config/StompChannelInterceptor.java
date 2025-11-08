package com.shuttleverse.connect.config;

import com.shuttleverse.connect.security.JwtTokenProvider;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class StompChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  public StompChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = accessor.getFirstNativeHeader("Authorization");
      String userId = null;

      if (token != null) {
        String extractedToken = jwtTokenProvider.extractTokenFromHeader(token);
        if (extractedToken != null && jwtTokenProvider.validateToken(extractedToken)) {
          userId = jwtTokenProvider.getUserIdFromToken(extractedToken);
        }
      }

      if (userId == null) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
          userId = (String) sessionAttributes.get("userId");
        }
      }

      final String finalUserId = userId;
      if (finalUserId != null) {
        accessor.setUser(() -> finalUserId);
      }
    }

    return message;
  }
}
