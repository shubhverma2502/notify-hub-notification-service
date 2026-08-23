package com.notifyhub.emailservice.consumer;

import com.notifyhub.emailservice.constant.KafkaTopics;
import com.notifyhub.emailservice.dto.EmailEvent;
import com.notifyhub.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailEventConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = KafkaTopics.EMAIL_NOTIFICATION_TOPIC,
            groupId = "email-service-group"
    )
    public void consume(EmailEvent event) {

        log.info("Received Email Event : {}", event);

        emailService.processEmailEvent(event);

    }


    @KafkaListener(
            topics = KafkaTopics.EMAIL_NOTIFICATION_RETRY_TOPIC,
            groupId = "email-service-group"
    )
    public void consumeRetry(EmailEvent event) {

        log.info(
                "Received Retry Email Event. NotificationId={}, retryCount={}",
                event.getNotificationId(),
                event.getRetryCount()
        );

        emailService.processRetryEmailEvent(event);
    }


    @KafkaListener(
            topics = KafkaTopics.EMAIL_NOTIFICATION_DLT_TOPIC,
            groupId = "email-service-group"
    )
    public void consumeDLT(EmailEvent event) {

        log.info("Received DLT Email Event : {}", event);

        emailService.processDeadLetterEvent(event);

    }
}
