package com.notifyhub.emailservice.service;

import com.notifyhub.emailservice.entity.EmailLog;
import com.notifyhub.emailservice.enums.EmailStatus;
import com.notifyhub.emailservice.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailLogServiceImpl implements EmailLogService {

    private final EmailLogRepository emailLogRepository;

    @Override
    public void logSuccess(String notificationId,
                           Integer attempt,
                           String providerResponse,
                           Long processingTimeMs) {

        EmailLog log = EmailLog.builder()
                .notificationId(notificationId)
                .attempt(attempt)
                .status(EmailStatus.SENT)
                .providerResponse(providerResponse)
                .processingTimeMs(processingTimeMs)
                .build();

        emailLogRepository.save(log);
    }

    @Override
    public void logFailure(String notificationId,
                           Integer attempt,
                           String errorMessage,
                           Long processingTimeMs) {

        EmailLog log = EmailLog.builder()
                .notificationId(notificationId)
                .attempt(attempt)
                .status(EmailStatus.FAILED)
                .errorMessage(errorMessage)
                .processingTimeMs(processingTimeMs)
                .build();

        emailLogRepository.save(log);
    }
}
