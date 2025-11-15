package com.shuttleverse.connect.repository;

import com.shuttleverse.connect.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, UUID> {
    
    List<PushSubscription> findByUserId(UUID userId);
    
    Optional<PushSubscription> findByEndpoint(String endpoint);
    
    void deleteByUserId(UUID userId);
    
    void deleteByEndpoint(String endpoint);
    
    boolean existsByEndpoint(String endpoint);
    
    boolean existsByUserIdAndEndpoint(UUID userId, String endpoint);
}

