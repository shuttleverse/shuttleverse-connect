package com.shuttleverse.connect.repository;

import com.shuttleverse.connect.entity.Chat;
import com.shuttleverse.connect.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("SELECT DISTINCT c FROM Chat c " +
            "JOIN ChatParticipant cp ON c.id = cp.chat.id " +
            "WHERE cp.userId = :userId " +
            "ORDER BY c.updatedAt DESC")
    List<Chat> findByParticipantUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Chat c " +
            "JOIN ChatParticipant cp1 ON c.id = cp1.chat.id " +
            "JOIN ChatParticipant cp2 ON c.id = cp2.chat.id " +
            "WHERE c.type = :type " +
            "AND ((cp1.userId = :user1Id AND cp2.userId = :user2Id) " +
            "     OR (cp1.userId = :user2Id AND cp2.userId = :user1Id)) " +
            "AND (SELECT COUNT(cp) FROM ChatParticipant cp WHERE cp.chat.id = c.id) = 2")
    Optional<Chat> findDirectChatBetweenUsers(
            @Param("type") ChatType type,
            @Param("user1Id") UUID user1Id,
            @Param("user2Id") UUID user2Id);
}
