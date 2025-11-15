package com.shuttleverse.connect.service;

import com.shuttleverse.connect.dto.NotificationPayload;
import com.shuttleverse.connect.entity.PushSubscription;
import com.shuttleverse.connect.repository.PushSubscriptionRepository;
import com.shuttleverse.connect.util.PushNotificationUtils;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationService {

  private final PushService pushService;
  private final PushSubscriptionRepository subscriptionRepository;

  public boolean sendPushNotification(PushSubscription subscription, NotificationPayload payload) {
    try {
      Subscription webPushSubscription = convertToWebPushSubscription(subscription);
      String jsonPayload = PushNotificationUtils.toJson(payload);
      Notification notification = new Notification(webPushSubscription, jsonPayload);

      pushService.send(notification);

      log.debug("Push notification sent successfully to endpoint: {}", subscription.getEndpoint());
      return true;

    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause != null && cause.getMessage() != null) {
        // Check for HTTP 410 (Gone) - subscription expired
        if (cause.getMessage().contains("410") || cause.getMessage().contains("Gone")) {
          log.info("Subscription expired, removing: {}", subscription.getEndpoint());
          subscriptionRepository.deleteByEndpoint(subscription.getEndpoint());
          return false;
        }
      }
      log.error("Failed to send push notification to endpoint: {}", subscription.getEndpoint(), e);
      return false;

    } catch (GeneralSecurityException | IOException e) {
      log.error("Error sending push notification to endpoint: {}", subscription.getEndpoint(), e);
      return false;
    } catch (Exception e) {
      log.error("Unexpected error sending push notification to endpoint: {}",
          subscription.getEndpoint(), e);
      return false;
    }
  }

  public void sendPushNotificationToUser(UUID userId, NotificationPayload payload) {
    List<PushSubscription> subscriptions = subscriptionRepository.findByUserId(userId);

    if (subscriptions.isEmpty()) {
      log.debug("No push subscriptions found for user: {}", userId);
      return;
    }

    for (PushSubscription subscription : subscriptions) {
      sendPushNotification(subscription, payload);
    }
  }

  public void sendPushNotificationToUsers(List<UUID> userIds, NotificationPayload payload) {
    for (UUID userId : userIds) {
      sendPushNotificationToUser(userId, payload);
    }
  }

  private Subscription convertToWebPushSubscription(PushSubscription subscription) {
    Subscription.Keys keys = new Subscription.Keys(
        subscription.getP256dh(),
        subscription.getAuth()
    );

    return new Subscription(subscription.getEndpoint(), keys);
  }
}

