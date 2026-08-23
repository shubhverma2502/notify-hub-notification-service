package com.notifyhub.emailservice.dto;

import java.time.LocalDateTime;

import com.notifyhub.emailservice.enums.EmailStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailResponse {

    private String notificationId;

    private EmailStatus status;

    private String message;

    private LocalDateTime timestamp;

}