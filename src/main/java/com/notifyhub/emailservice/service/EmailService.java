package com.notifyhub.emailservice.service;

import com.notifyhub.emailservice.dto.EmailEvent;
import com.notifyhub.emailservice.dto.EmailRequest;
import com.notifyhub.emailservice.dto.EmailResponse;

public interface EmailService {

    EmailResponse sendEmail(EmailRequest request);

    void processEmailEvent(EmailEvent event);

    void processRetryEmailEvent(EmailEvent event);

    void processDeadLetterEvent(EmailEvent event);

}
