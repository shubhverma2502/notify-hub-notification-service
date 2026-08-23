package com.notifyhub.emailservice.config;

import com.notifyhub.emailservice.constant.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic emailTopic() {
        return new NewTopic(
                KafkaTopics.EMAIL_NOTIFICATION_TOPIC,
                3,
                (short) 1
        );
    }

    @Bean
    public NewTopic retryTopic() {
        return new NewTopic(
                KafkaTopics.EMAIL_NOTIFICATION_RETRY_TOPIC,
                3,
                (short) 1
        );
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return new NewTopic(
                KafkaTopics.EMAIL_NOTIFICATION_DLT_TOPIC,
                3,
                (short) 1
        );
    }
}
