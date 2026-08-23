package com.notifyhub.emailservice.service;

import com.notifyhub.emailservice.constant.RetryConstants;
import com.notifyhub.emailservice.dto.EmailEvent;
import com.notifyhub.emailservice.dto.EmailRequest;
import com.notifyhub.emailservice.dto.EmailResponse;
import com.notifyhub.emailservice.dto.ProcessedEmailTemplate;
import com.notifyhub.emailservice.entity.EmailNotification;
import com.notifyhub.emailservice.enums.EmailStatus;
import com.notifyhub.emailservice.exception.EmailSendingException;
import com.notifyhub.emailservice.producer.EmailEventProducer;
import com.notifyhub.emailservice.repository.EmailNotificationRepository;
import com.notifyhub.emailservice.util.RetryUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final MailService mailService;
    private final EmailNotificationRepository emailNotificationRepository;
    private final TemplateService templateService;
    private final EmailLogService emailLogService;
    private final EmailEventProducer emailEventProducer;

    @Override
    public EmailResponse sendEmail(EmailRequest request) {

        long startTime = System.currentTimeMillis();

        ProcessedEmailTemplate template =
                templateService.processTemplate(
                        request.getTemplateName(),
                        request.getVariables()
                );

        EmailNotification emailNotification =
                emailNotificationRepository
                        .findByNotificationId(request.getNotificationId())
                        .orElseGet(() -> EmailNotification.builder()
                                .notificationId(request.getNotificationId())
                                .recipient(request.getRecipient())
                                .recipientName(request.getRecipientName())
                                .templateName(request.getTemplateName())
                                .priority(request.getPriority())
                                .retryCount(0)
                                .build());

        emailNotification.setRecipient(request.getRecipient());
        emailNotification.setRecipientName(request.getRecipientName());
        emailNotification.setTemplateName(request.getTemplateName());
        emailNotification.setPriority(request.getPriority());
        emailNotification.setSubject(template.getSubject());

        try {

            log.info(
                    "Attempting to send email. NotificationId={}, RetryCount={}",
                    emailNotification.getNotificationId(),
                    emailNotification.getRetryCount()
            );

            mailService.sendEmail(
                    request.getRecipient(),
                    template.getSubject(),
                    template.getBody()
            );

            // SUCCESS
            emailNotification.setStatus(EmailStatus.SENT);
            emailNotification.setSentAt(LocalDateTime.now());
            emailNotification.setFailureReason(null);
            emailNotification.setScheduledAt(null);

            emailNotification =
                    emailNotificationRepository.save(emailNotification);

            long processingTime =
                    System.currentTimeMillis() - startTime;

            emailLogService.logSuccess(
                    emailNotification.getNotificationId(),
                    emailNotification.getRetryCount() + 1,
                    "SMTP accepted the email.",
                    processingTime
            );

            log.info(
                    "Email sent successfully. NotificationId={}",
                    emailNotification.getNotificationId()
            );

            return mapToResponse(emailNotification);

        } catch (EmailSendingException e) {

            long processingTime = System.currentTimeMillis() - startTime;

            int currentRetryCount = emailNotification.getRetryCount();

            int nextRetryCount = currentRetryCount + 1;

            String failureReason = e.getMessage();

            log.error(
                    "Email sending failed. NotificationId={}, RetryCount={}, Error={}",
                    emailNotification.getNotificationId(),
                    nextRetryCount,
                    failureReason,
                    e
            );

            emailLogService.logFailure(
                    emailNotification.getNotificationId(),
                    nextRetryCount,
                    failureReason,
                    processingTime
            );

            /*
             * Retry available
             */
            if (nextRetryCount <= RetryConstants.MAX_RETRY_COUNT) {

                long delay =
                        RetryUtil.getDelay(currentRetryCount);

                emailNotification.setRetryCount(nextRetryCount);
                emailNotification.setStatus(EmailStatus.RETRYING);
                emailNotification.setFailureReason(e.getMessage());
                emailNotification.setScheduledAt(
                        LocalDateTime.now().plusSeconds(delay)
                );

                emailNotificationRepository.save(emailNotification);

                log.warn(
                        "Email scheduled for retry. NotificationId={}, RetryCount={}, RetryAt={}",
                        emailNotification.getNotificationId(),
                        nextRetryCount,
                        emailNotification.getScheduledAt()
                );

            }
            /*
             * Maximum retries reached
             */
            else {

                emailNotification.setStatus(EmailStatus.FAILED);

                emailNotification.setFailureReason(
                        "Maximum retry attempts exceeded."
                );

                emailNotification.setScheduledAt(null);

                emailNotificationRepository.save(emailNotification);

                EmailEvent dltEvent = EmailEvent.builder()
                        .notificationId(emailNotification.getNotificationId())
                        .recipient(emailNotification.getRecipient())
                        .recipientName(emailNotification.getRecipientName())
                        .templateName(emailNotification.getTemplateName())
                        .priority(emailNotification.getPriority())
                        .variables(request.getVariables())
                        .retryCount(currentRetryCount)
                        .build();

                emailEventProducer.publishDltEvent(dltEvent);

                log.error(
                        "Maximum retry attempts exceeded. NotificationId={}",
                        emailNotification.getNotificationId()
                );
            }

            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    public EmailResponse mapToResponse(EmailNotification notification) {

        return EmailResponse.builder()
                .notificationId(notification.getNotificationId())
                .status(notification.getStatus())
                .message(getMessage(notification.getStatus()))
                .timestamp(notification.getCreatedAt())
                .build();

    }

    private String getMessage(EmailStatus status) {
        return switch (status) {
            case PENDING -> "Email is queued for processing.";
            case PROCESSING -> "Email is being processed.";
            case SENT -> "Email sent successfully.";
            case FAILED -> "Email sending failed.";
            case RETRYING -> "Email is being retried.";
            case DELIVERED -> "Email delivered successfully.";
            case CANCELLED -> "Email notification was cancelled.";
        };
    }

    @Override
    public void processEmailEvent(EmailEvent event) {

        try {

            EmailRequest request = EmailRequest.builder()
                    .notificationId(event.getNotificationId())
                    .recipient(event.getRecipient())
                    .recipientName(event.getRecipientName())
                    .templateName(event.getTemplateName())
                    .priority(event.getPriority())
                    .variables(event.getVariables())
                    .build();

            sendEmail(request);

        } catch (Exception e) {

            log.error(
                    "Failed to process email Kafka event. NotificationId={}",
                    event.getNotificationId(),
                    e
            );
        }
    }

    @Override
    public void processRetryEmailEvent(EmailEvent event) {

        log.info(
                "Received retry event: notificationId={}, eventRetryCount={}",
                event.getNotificationId(),
                event.getRetryCount()
        );

        EmailNotification notification =
                emailNotificationRepository
                        .findByNotificationId(event.getNotificationId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found: "
                                                + event.getNotificationId()
                                ));

        /*
         * Do not process stale/duplicate retry events.
         */
        if (notification.getRetryCount() > RetryConstants.MAX_RETRY_COUNT) {

            log.warn(
                    "Ignoring retry event. Maximum retry count already reached. " +
                            "NotificationId={}, DBRetryCount={}, EventRetryCount={}",
                    notification.getNotificationId(),
                    notification.getRetryCount(),
                    event.getRetryCount()
            );

            return;
        }

        /*
         * Do not retry a notification that has already succeeded.
         */
        if (notification.getStatus() == EmailStatus.SENT) {

            log.warn(
                    "Ignoring retry event because email was already sent. NotificationId={}",
                    notification.getNotificationId()
            );

            return;
        }

        log.info(
                "Processing retry {}/{} for NotificationId={}",
                notification.getRetryCount() + 1,
                RetryConstants.MAX_RETRY_COUNT,
                notification.getNotificationId()
        );

        try {

            EmailRequest request = EmailRequest.builder()
                    .notificationId(notification.getNotificationId())
                    .recipient(notification.getRecipient())
                    .recipientName(notification.getRecipientName())
                    .templateName(notification.getTemplateName())
                    .priority(notification.getPriority())
                    .variables(event.getVariables())
                    .build();

            sendEmail(request);

        } catch (Exception e) {

            /*
             * IMPORTANT:
             * Do NOT rethrow the exception to Kafka.
             *
             * Our application already handled the retry logic
             * inside sendEmail().
             */
            log.error(
                    "Retry processing failed. NotificationId={}",
                    event.getNotificationId(),
                    e
            );
        }
    }

    @Override
    public void processDeadLetterEvent(EmailEvent event) {

        log.error(
                "Email moved to Dead Letter Topic. NotificationId={}",
                event.getNotificationId()
        );

        EmailNotification notification =
                emailNotificationRepository
                        .findByNotificationId(event.getNotificationId())
                        .orElseThrow(() ->
                                new RuntimeException("Notification not found"));

        // Prevent duplicate DLT processing
        if (notification.getStatus() == EmailStatus.FAILED) {

            log.warn(
                    "Notification already marked FAILED. Ignoring duplicate DLT event. NotificationId={}",
                    event.getNotificationId()
            );

            return;
        }

        notification.setStatus(EmailStatus.FAILED);
        notification.setFailureReason(
                "Maximum retry attempts exceeded."
        );

        emailNotificationRepository.save(notification);

        emailLogService.logFailure(
                notification.getNotificationId(),
                notification.getRetryCount(),
                "Moved to Dead Letter Topic",
                0L
        );
    }
}
