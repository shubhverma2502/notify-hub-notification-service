package com.notifyhub.emailservice.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String EMAIL_NOTIFICATION_TOPIC = "notification.email";
    public static final String EMAIL_NOTIFICATION_RETRY_TOPIC = "notification.email.retry";
    public static final String EMAIL_NOTIFICATION_DLT_TOPIC = "notification.email.dlt";

}
