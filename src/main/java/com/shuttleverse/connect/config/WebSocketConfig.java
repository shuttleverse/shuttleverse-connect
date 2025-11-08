package com.shuttleverse.connect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final AuthHandshakeInterceptor authHandshakeInterceptor;
  private final StompChannelInterceptor stompChannelInterceptor;

  public WebSocketConfig(
      AuthHandshakeInterceptor authHandshakeInterceptor,
      StompChannelInterceptor stompChannelInterceptor) {
    this.authHandshakeInterceptor = authHandshakeInterceptor;
    this.stompChannelInterceptor = stompChannelInterceptor;
  }

  @Override
  public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .addInterceptors(authHandshakeInterceptor)
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void configureClientInboundChannel(
      @NonNull org.springframework.messaging.simp.config.ChannelRegistration registration) {
    registration.interceptors(stompChannelInterceptor);
  }
}
