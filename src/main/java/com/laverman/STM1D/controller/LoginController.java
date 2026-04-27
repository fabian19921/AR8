package com.laverman.STM1D.controller;

import com.laverman.STM1D.dto.LoginResponse;
import com.laverman.STM1D.model.User;
import com.laverman.STM1D.repository.UserRepository;
import com.laverman.STM1D.service.JwtService;
import com.laverman.STM1D.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public LoginController(UserRepository userRepository,
                           PasswordService passwordService,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Ongeldige email of wachtwoord");
        }

        User user = optionalUser.get();

        boolean passwordCorrect = passwordService.verifyPassword(
                loginRequest.getPassword(),
                user.getPassword()
        );

        if (!passwordCorrect) {
            return ResponseEntity.status(401).body("Ongeldige email of wachtwoord");
        }

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);
            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(401).body("Gebruiker niet gevonden");
            }

            User user = optionalUser.get();
            Map<String, String> response = new HashMap<>();
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());


            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Ongeldige token");
        }
    }
}
