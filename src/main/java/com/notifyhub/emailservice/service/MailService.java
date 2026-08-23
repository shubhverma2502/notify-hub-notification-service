package com.notifyhub.emailservice.service;

public interface MailService {

    void sendEmail(String recipient, String subject, String html);
}
