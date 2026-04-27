package com.laverman.STM1D.controller;

import com.laverman.STM1D.model.PasswordResetToken;
import com.laverman.STM1D.model.User;
import com.laverman.STM1D.repository.PasswordResetRepository;
import com.laverman.STM1D.repository.UserRepository;
import com.laverman.STM1D.service.EmailService;
import com.laverman.STM1D.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordService passwordService;

    public PasswordResetController(UserRepository userRepository,
                                   PasswordResetRepository tokenRepository,
                                   EmailService emailService,
                                   PasswordService passwordService) {
        this.userRepository  = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService    = emailService;
        this.passwordService = passwordService;
    }

    // Stap 1: reset aanvragen
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Altijd zelfde response zodat je niet kunt raden welke emails bestaan
        if (optionalUser.isEmpty()) {
            return ResponseEntity.ok("Als dit email bekend is, ontvang je een reset link.");
        }

        User user = optionalUser.get();

        // Genereer unieke token
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // Stuur email
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);

        return ResponseEntity.ok("Als dit email bekend is, ontvang je een reset link.");
    }

    // Stap 2: token valideren
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(400).body("Ongeldige link");
        }

        PasswordResetToken resetToken = optionalToken.get();

        if (resetToken.isUsed()) {
            return ResponseEntity.status(400).body("Deze link is al gebruikt");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Deze link is verlopen");
        }

        return ResponseEntity.ok("Geldig");
    }

    // Stap 3: nieuw wachtwoord instellen
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token    = body.get("token");
        String password = body.get("password");
        String confirm  = body.get("confirm");

        if (!password.equals(confirm)) {
            return ResponseEntity.status(400).body("Wachtwoorden komen niet overeen");
        }

        if (password.length() < 6) {
            return ResponseEntity.status(400).body("Wachtwoord moet minimaal 6 tekens zijn");
        }

        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(400).body("Ongeldige link");
        }

        PasswordResetToken resetToken = optionalToken.get();

        if (resetToken.isUsed()) {
            return ResponseEntity.status(400).body("Deze link is al gebruikt");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Deze link is verlopen");
        }

        // Wachtwoord updaten
        User user = resetToken.getUser();
        user.setPassword(passwordService.hashPassword(password));
        userRepository.save(user);

        // Token markeren als gebruikt
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return ResponseEntity.ok("Wachtwoord gewijzigd");
    }
}