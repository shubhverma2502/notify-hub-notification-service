package com.notifyhub.emailservice.service;

import com.notifyhub.emailservice.dto.ProcessedEmailTemplate;

import java.util.Map;

public interface TemplateService {

//    String processTemplate(String templateName,
//                           Map<String, Object> variables);

    ProcessedEmailTemplate processTemplate(
            String templateName,
            Map<String, Object> variables
    );
}
