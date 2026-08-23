package com.notifyhub.emailservice.service;

import com.notifyhub.emailservice.exception.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService{

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String recipient, String subject, String html) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromEmail);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(html, true);

            javaMailSender.send(mimeMessage);

        } catch (Exception ex) {
            log.error(
                    "Email sending failed. Recipient={}, Error={}",
                    recipient,
                    ex.getMessage(),
                    ex
            );

            throw new EmailSendingException(
                    extractErrorMessage(ex),
                    ex
            );
        }

    }
    private String extractErrorMessage(Throwable ex) {

        Throwable cause = ex;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause.getMessage() != null
                ? cause.getMessage()
                : ex.getMessage();
    }
}
