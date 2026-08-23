package com.notifyhub.emailservice.entity;

import java.time.LocalDateTime;

import com.notifyhub.emailservice.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "email_logs",
        indexes = {
                @Index(name = "idx_notification_id", columnList = "notificationId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String notificationId;

    @Column(nullable = false)
    private Integer attempt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus status;

    @Column(columnDefinition = "TEXT")
    private String providerResponse;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Long processingTimeMs;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}