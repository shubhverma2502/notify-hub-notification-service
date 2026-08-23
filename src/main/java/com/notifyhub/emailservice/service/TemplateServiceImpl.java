package com.notifyhub.emailservice.service;

import com.notifyhub.emailservice.dto.ProcessedEmailTemplate;
import com.notifyhub.emailservice.entity.EmailTemplate;
import com.notifyhub.emailservice.exception.EmailTemplateNotFoundException;
import com.notifyhub.emailservice.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final SpringTemplateEngine springTemplateEngine;

    @Override
    public ProcessedEmailTemplate processTemplate(String templateName, Map<String, Object> variables) {

        EmailTemplate emailTemplate =
                emailTemplateRepository.findByTemplateName(templateName)
                        .orElseThrow(() -> new EmailTemplateNotFoundException("Template Not Found"));

        if(!Boolean.TRUE.equals(emailTemplate.getActive()))
            throw new RuntimeException("Template is Inactive");

        Context context = new Context();

        if (variables != null) {
            context.setVariables(variables);
        }

        String processedBody =
                springTemplateEngine.process(
                        emailTemplate.getBody(),
                        context
                );

        return ProcessedEmailTemplate.builder()
                .subject(emailTemplate.getSubject())
                .body(processedBody)
                .build();
    }
}
