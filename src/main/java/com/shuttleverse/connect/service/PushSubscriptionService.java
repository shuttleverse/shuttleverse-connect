package com.shuttleverse.connect.service;

import com.shuttleverse.connect.dto.request.PushSubscriptionRequest;
import com.shuttleverse.connect.dto.response.PushSubscriptionResponse;
import com.shuttleverse.connect.entity.PushSubscription;
import com.shuttleverse.connect.repository.PushSubscriptionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushSubscriptionService {

  private final PushSubscriptionRepository subscriptionRepository;

  /**
   * Subscribe user to push notifications If endpoint already exists, updates the subscription
   */
  @Transactional
  public PushSubscriptionResponse subscribe(UUID userId, PushSubscriptionRequest request) {
    Optional<PushSubscription> existing = subscriptionRepository.findByEndpoint(
        request.getEndpoint());

    if (existing.isPresent()) {
      PushSubscription subscription = existing.get();

      // Verify it belongs to this user
      if (!subscription.getUserId().equals(userId)) {
        throw new IllegalStateException("Subscription endpoint already exists for another user");
      }

      // Update existing subscription (keys might have changed)
      subscription.setP256dh(request.getKeys().getP256dh());
      subscription.setAuth(request.getKeys().getAuth());
      subscription.setUserAgent(request.getUserAgent());
      subscription.setDeviceType(detectDeviceType(request.getUserAgent()));

      subscription = subscriptionRepository.save(subscription);
      log.info("Updated push subscription for user: {}, endpoint: {}", userId,
          request.getEndpoint());
      return convertToResponse(subscription);
    }

    PushSubscription subscription = PushSubscription.builder()
        .userId(userId)
        .endpoint(request.getEndpoint())
        .p256dh(request.getKeys().getP256dh())
        .auth(request.getKeys().getAuth())
        .userAgent(request.getUserAgent())
        .deviceType(detectDeviceType(request.getUserAgent()))
        .build();

    subscription = subscriptionRepository.save(subscription);
    return convertToResponse(subscription);
  }

  @Transactional
  public void unsubscribe(String endpoint) {
    Optional<PushSubscription> subscription = subscriptionRepository.findByEndpoint(endpoint);
    if (subscription.isPresent()) {
      subscriptionRepository.deleteByEndpoint(endpoint);
      log.info("Unsubscribed from push notifications. Endpoint: {}", endpoint);
    } else {
      log.warn("Subscription not found for endpoint: {}", endpoint);
    }
  }

  @Transactional
  public void unsubscribeUser(UUID userId) {
    List<PushSubscription> subscriptions = subscriptionRepository.findByUserId(userId);
    subscriptionRepository.deleteByUserId(userId);
    log.info("Unsubscribed user from all push notifications. User: {}, Removed {} subscription(s)",
        userId, subscriptions.size());
  }

  public List<PushSubscriptionResponse> getUserSubscriptions(UUID userId) {
    return subscriptionRepository.findByUserId(userId)
        .stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
  }

  private String detectDeviceType(String userAgent) {
    if (userAgent == null) {
      return "unknown";
    }
    String ua = userAgent.toLowerCase();
    if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ipod")) {
      return "ios";
    } else if (ua.contains("android")) {
      return "android";
    } else if (ua.contains("windows") || ua.contains("mac") || ua.contains("linux")) {
      return "desktop";
    }
    return "unknown";
  }

  private PushSubscriptionResponse convertToResponse(PushSubscription subscription) {
    return PushSubscriptionResponse.builder()
        .id(subscription.getId())
        .userId(subscription.getUserId())
        .endpoint(subscription.getEndpoint())
        .deviceType(subscription.getDeviceType())
        .createdAt(subscription.getCreatedAt())
        .updatedAt(subscription.getUpdatedAt())
        .build();
  }
}

