package com.notifyhub.emailservice.repository;

import com.notifyhub.emailservice.entity.EmailNotification;
import com.notifyhub.emailservice.enums.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailNotificationRepository extends JpaRepository<EmailNotification,Long> {

    Optional<EmailNotification> findByNotificationId(String notificationId);

    boolean existsByNotificationId(String notificationId);

    List<EmailNotification> findByStatus(EmailStatus status);

    List<EmailNotification> findByRecipient(String recipient);

    List<EmailNotification> findByStatusAndScheduledAtLessThanEqual(EmailStatus status, LocalDateTime time);

}
