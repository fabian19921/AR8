package com.laverman.STM1D.controller;

import com.laverman.STM1D.model.Role;
import com.laverman.STM1D.model.User;
import com.laverman.STM1D.repository.UserRepository;
import com.laverman.STM1D.service.EmailService;
import com.laverman.STM1D.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final EmailService emailService;

    public RegisterController(UserRepository userRepository,
                              PasswordService passwordService,
                              EmailService emailService) {
        this.userRepository  = userRepository;
        this.passwordService = passwordService;
        this.emailService    = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is al in gebruik");
        }

        if (user.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Wachtwoord moet minimaal 6 tekens zijn");
        }

        user.setRole(Role.USER);
        user.setPassword(passwordService.hashPassword(user.getPassword()));

        User savedUser = userRepository.save(user);

        try {
            System.out.println("Welkomstmail versturen naar: " + savedUser.getEmail());
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
            System.out.println("Welkomstmail verstuurd!");
        } catch (Exception e) {
            System.err.println("Welkomstmail versturen mislukt: " + e.getMessage());
            e.printStackTrace();
        }

        savedUser.setPassword(null);
        return ResponseEntity.ok(savedUser);
    }
}