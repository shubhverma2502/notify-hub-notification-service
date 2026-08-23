package com.notifyhub.emailservice.controller;

import com.notifyhub.emailservice.dto.EmailEvent;
import com.notifyhub.emailservice.producer.EmailEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailEventController {

    private final EmailEventProducer emailEventProducer;

    @PostMapping("/events")
    public ResponseEntity<String> publishEmailEvent(
            @RequestBody EmailEvent event) {

        emailEventProducer.publish(event);

        return ResponseEntity.ok("Email event published successfully");
    }
}