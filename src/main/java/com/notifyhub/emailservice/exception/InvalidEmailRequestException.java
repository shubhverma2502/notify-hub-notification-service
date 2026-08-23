package com.notifyhub.emailservice.exception;

public class InvalidEmailRequestException extends RuntimeException{
    public InvalidEmailRequestException(String message) {
        super(message);
    }
}
