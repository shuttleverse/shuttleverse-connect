package com.shuttleverse.connect.controller.rest;

import com.shuttleverse.connect.dto.external.SVApiResponse;
import com.shuttleverse.connect.dto.request.PushSubscriptionRequest;
import com.shuttleverse.connect.dto.response.PushSubscriptionResponse;
import com.shuttleverse.connect.service.PushSubscriptionService;
import com.shuttleverse.connect.util.UserContext;
import com.shuttleverse.connect.util.UserIdConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class PushSubscriptionController {

    private final PushSubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<SVApiResponse<PushSubscriptionResponse>> subscribe(
            @Valid @RequestBody PushSubscriptionRequest request) {
        String userIdStr = UserContext.getCurrentUserId();
        UUID userId = UserIdConverter.toUuid(userIdStr);
        
        PushSubscriptionResponse response = subscriptionService.subscribe(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SVApiResponse.success(response));
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<SVApiResponse<Void>> unsubscribe(
            @RequestParam String endpoint) {
        subscriptionService.unsubscribe(endpoint);
        return ResponseEntity.ok(SVApiResponse.success("Unsubscribed successfully", null));
    }

    @DeleteMapping("/unsubscribe-all")
    public ResponseEntity<SVApiResponse<Void>> unsubscribeAll() {
        String userIdStr = UserContext.getCurrentUserId();
        UUID userId = UserIdConverter.toUuid(userIdStr);
        
        subscriptionService.unsubscribeUser(userId);
        return ResponseEntity.ok(SVApiResponse.success("Unsubscribed from all devices", null));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<SVApiResponse<List<PushSubscriptionResponse>>> getSubscriptions() {
        String userIdStr = UserContext.getCurrentUserId();
        UUID userId = UserIdConverter.toUuid(userIdStr);
        
        List<PushSubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(SVApiResponse.success(subscriptions));
    }
}

