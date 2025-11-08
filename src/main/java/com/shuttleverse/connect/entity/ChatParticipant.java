package com.shuttleverse.connect.entity;

import com.shuttleverse.connect.enums.ParticipantRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipant {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_id", nullable = false)
  private Chat chat;

  @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
  private UUID userId;

  @Column(nullable = false)
  private LocalDateTime joinedAt;

  @Column
  private LocalDateTime lastReadAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private ParticipantRole role = ParticipantRole.MEMBER;

  @PrePersist
  protected void onCreate() {
    if (joinedAt == null) {
      joinedAt = LocalDateTime.now();
    }
  }
}

