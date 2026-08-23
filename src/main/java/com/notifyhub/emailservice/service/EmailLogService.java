package com.notifyhub.emailservice.service;

public interface EmailLogService {

    void logSuccess(String notificationId,
                    Integer attempt,
                    String providerResponse,
                    Long processingTimeMs);

    void logFailure(String notificationId,
                    Integer attempt,
                    String errorMessage,
                    Long processingTimeMs);
}
