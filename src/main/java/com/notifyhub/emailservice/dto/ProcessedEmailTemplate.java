package com.notifyhub.emailservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessedEmailTemplate {

    private String subject;

    private String body;
}
