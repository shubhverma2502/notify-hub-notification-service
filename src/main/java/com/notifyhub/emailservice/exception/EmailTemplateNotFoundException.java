package com.notifyhub.emailservice.exception;

public class EmailTemplateNotFoundException extends RuntimeException {
    public EmailTemplateNotFoundException(String message) {
        super(message);
    }
}
