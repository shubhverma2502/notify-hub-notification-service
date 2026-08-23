package com.notifyhub.emailservice.controller;

import com.notifyhub.emailservice.dto.EmailRequest;
import com.notifyhub.emailservice.dto.EmailResponse;
import com.notifyhub.emailservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/email")
@RequiredArgsConstructor
public class EmailNotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<EmailResponse> sendNotification(
            @Valid @RequestBody EmailRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(emailService.sendEmail(request));
    }
}
