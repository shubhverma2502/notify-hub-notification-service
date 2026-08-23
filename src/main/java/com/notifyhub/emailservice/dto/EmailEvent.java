package com.notifyhub.emailservice.dto;

import java.time.LocalDateTime;
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
public class EmailEvent {

    private String notificationId;

    private Long userId;

    @Email
    @NotBlank
    private String recipient;

    private String recipientName;

    @NotBlank
    private String templateName;

    private Map<String, Object> variables;

    private EmailPriority priority;

    private LocalDateTime scheduledAt;

    private Integer retryCount;

}