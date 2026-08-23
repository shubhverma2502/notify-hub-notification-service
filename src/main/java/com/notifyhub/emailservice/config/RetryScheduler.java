package com.notifyhub.emailservice.config;

import com.notifyhub.emailservice.dto.EmailEvent;
import com.notifyhub.emailservice.entity.EmailNotification;
import com.notifyhub.emailservice.enums.EmailStatus;
import com.notifyhub.emailservice.producer.EmailEventProducer;
import com.notifyhub.emailservice.repository.EmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class RetryScheduler {

    private final EmailNotificationRepository emailNotificationRepository;
    private final EmailEventProducer emailEventProducer;

    @Scheduled(fixedDelay = 5000)
    public void processRetryEmails() {

        List<EmailNotification> notifications =
                emailNotificationRepository
                        .findByStatusAndScheduledAtLessThanEqual(
                                EmailStatus.RETRYING,
                                LocalDateTime.now()
                        );

        if (notifications.isEmpty()) {
            return;
        }

        log.info(
                "Found {} email(s) ready for retry.",
                notifications.size()
        );

        for (EmailNotification notification : notifications) {

            log.info(
                    "Publishing retry event. NotificationId={}, RetryCount={}",
                    notification.getNotificationId(),
                    notification.getRetryCount()
            );

            EmailEvent event =
                    EmailEvent.builder()
                            .notificationId(
                                    notification.getNotificationId())
                            .recipient(
                                    notification.getRecipient())
                            .recipientName(
                                    notification.getRecipientName())
                            .templateName(
                                    notification.getTemplateName())
                            .priority(
                                    notification.getPriority())
                            .retryCount(
                                    notification.getRetryCount())
                            .build();

            /*
             * IMPORTANT:
             * Change status BEFORE publishing.
             *
             * This prevents the next scheduler execution
             * from selecting the same notification again.
             */
            notification.setStatus(EmailStatus.PROCESSING);

            notification.setScheduledAt(null);

            emailNotificationRepository.save(notification);

            emailEventProducer.publishRetryEvent(event);

            log.info(
                    "Retry event published successfully. NotificationId={}",
                    notification.getNotificationId()
            );
        }
    }
}