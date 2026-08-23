package com.notifyhub.emailservice.entity;

import java.time.LocalDateTime;

import com.notifyhub.emailservice.enums.EmailPriority;
import com.notifyhub.emailservice.enums.EmailStatus;
import com.notifyhub.emailservice.enums.NotificationChannel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "email_notifications",
        indexes = {
                @Index(name = "idx_notification_id", columnList = "notificationId"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_recipient", columnList = "recipient")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String notificationId;

    @Column
    private Long userId;

    @Column(nullable = false, length = 255)
    private String recipient;

    @Column
    private String recipientName;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(nullable = false, length = 100)
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailPriority priority;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    private Integer retryCount;

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null)
            status = EmailStatus.PENDING;

        if (priority == null)
            priority = EmailPriority.MEDIUM;

        if (channel == null)
            channel = NotificationChannel.EMAIL;

        if (retryCount == null)
            retryCount = 0;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}