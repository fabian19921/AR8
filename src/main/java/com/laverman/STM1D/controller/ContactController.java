package com.laverman.STM1D.controller;

import com.laverman.STM1D.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> sendContact(@RequestBody Map<String, String> body) {
        try {
            String name    = body.getOrDefault("name",    "Onbekend");
            String email   = body.getOrDefault("email",   "");
            String subject = body.getOrDefault("subject", "Geen onderwerp");
            String message = body.getOrDefault("message", "");

            if (message.isBlank()) {
                return ResponseEntity.badRequest().body("Vul een bericht in");
            }

            emailService.sendContactEmail(name, email, subject, message);
            return ResponseEntity.ok("Bericht verzonden");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Verzenden mislukt: " + e.getMessage());
        }
    }
}