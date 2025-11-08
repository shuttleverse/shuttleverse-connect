package com.shuttleverse.connect.repository;

import com.shuttleverse.connect.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

  Page<Message> findByChatIdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);

  @Query("SELECT COUNT(m) FROM Message m " +
      "JOIN ChatParticipant cp ON m.chat.id = cp.chat.id " +
      "WHERE m.chat.id = :chatId " +
      "AND cp.userId = :userId " +
      "AND m.senderId != :userId " +
      "AND m.deletedAt IS NULL " +
      "AND (cp.lastReadAt IS NULL OR m.createdAt > cp.lastReadAt)")
  long countUnreadMessages(@Param("chatId") UUID chatId, @Param("userId") UUID userId);

  @Query("SELECT m FROM Message m " +
      "WHERE m.chat.id = :chatId " +
      "AND m.senderId != :userId " +
      "AND m.deletedAt IS NULL " +
      "ORDER BY m.createdAt DESC")
  Page<Message> findUnreadMessagesByChatIdAndUserId(
      @Param("chatId") UUID chatId,
      @Param("userId") UUID userId,
      Pageable pageable);
}

