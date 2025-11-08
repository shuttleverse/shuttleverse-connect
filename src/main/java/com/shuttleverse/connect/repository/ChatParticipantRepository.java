package com.shuttleverse.connect.repository;

import com.shuttleverse.connect.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {

  List<ChatParticipant> findByChatId(UUID chatId);

  Optional<ChatParticipant> findByChatIdAndUserId(UUID chatId, UUID userId);

  boolean existsByChatIdAndUserId(UUID chatId, UUID userId);

  @Query("SELECT cp.userId FROM ChatParticipant cp WHERE cp.chat.id = :chatId")
  List<UUID> findUserIdsByChatId(@Param("chatId") UUID chatId);
}

