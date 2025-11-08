package com.shuttleverse.connect.entity;

import com.shuttleverse.connect.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_id", nullable = false)
  private Chat chat;

  @Column(name = "sender_id", nullable = false, columnDefinition = "UUID")
  private UUID senderId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime editedAt;

  @Column
  private LocalDateTime deletedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private MessageStatus status = MessageStatus.SENT;

  @Column
  private LocalDateTime readAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}

