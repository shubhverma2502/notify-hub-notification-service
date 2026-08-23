package com.notifyhub.emailservice.dto;

import java.util.Map;

import com.notifyhub.emailservice.enums.EmailPriority;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {

    @NotBlank
    private String notificationId;

    @Email
    @NotBlank
    private String recipient;

    private String recipientName;

    @NotBlank
    private String subject;

    @NotBlank
    private String templateName;

    private Map<String, Object> variables;

    private EmailPriority priority;

}