package com.notifyhub.emailservice.producer;

import com.notifyhub.emailservice.constant.KafkaTopics;
import com.notifyhub.emailservice.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailEventProducer {

    private final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    public void publish(EmailEvent event) {

        log.info("Publishing email event : {}", event.getNotificationId());

        kafkaTemplate.send(
                KafkaTopics.EMAIL_NOTIFICATION_TOPIC,
                event.getNotificationId(),
                event);

    }

    public void publishRetryEvent(EmailEvent event) {

        log.info("Publishing retry event : {}", event.getNotificationId());

        kafkaTemplate.send(
                KafkaTopics.EMAIL_NOTIFICATION_RETRY_TOPIC,
                event.getNotificationId(),
                event);
    }

    public void publishDltEvent(EmailEvent event) {

        log.info("Publishing DLT event : {}", event.getNotificationId());

        kafkaTemplate.send(
                KafkaTopics.EMAIL_NOTIFICATION_DLT_TOPIC,
                event.getNotificationId(),
                event);
    }

}
