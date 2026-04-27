package com.laverman.STM1D.controller;

import com.laverman.STM1D.model.User;
import com.laverman.STM1D.repository.UserRepository;
import com.laverman.STM1D.service.JwtService;
import com.laverman.STM1D.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordService passwordService;

    public ProfileController(UserRepository userRepository,
                             JwtService jwtService,
                             PasswordService passwordService) {
        this.userRepository  = userRepository;
        this.jwtService      = jwtService;
        this.passwordService = passwordService;
    }

    // Profiel ophalen
    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = getUserFromHeader(authHeader);

            return ResponseEntity.ok(Map.of(
                    "name",      user.getName(),
                    "email",     user.getEmail(),
                    "birthDate", user.getBirthDate()  != null ? user.getBirthDate()  : "",
                    "location",  user.getLocation()   != null ? user.getLocation()   : "",
                    "quote",     user.getQuote()      != null ? user.getQuote()      : "",
                    "role",      user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // Profiel opslaan
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Map<String, String> body) {
        try {
            User user = getUserFromHeader(authHeader);

            if (body.containsKey("name")      && !body.get("name").isBlank())
                user.setName(body.get("name"));
            if (body.containsKey("birthDate"))
                user.setBirthDate(body.get("birthDate"));
            if (body.containsKey("location"))
                user.setLocation(body.get("location"));
            if (body.containsKey("quote"))
                user.setQuote(body.get("quote"));

            userRepository.save(user);
            return ResponseEntity.ok("Profiel opgeslagen");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // Wachtwoord wijzigen
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody Map<String, String> body) {
        try {
            User user = getUserFromHeader(authHeader);

            String huidig    = body.get("current");
            String nieuw     = body.get("new");
            String bevestig  = body.get("confirm");

            if (!passwordService.verifyPassword(huidig, user.getPassword())) {
                return ResponseEntity.status(400).body("Huidig wachtwoord klopt niet");
            }

            if (!nieuw.equals(bevestig)) {
                return ResponseEntity.status(400).body("Wachtwoorden komen niet overeen");
            }

            if (nieuw.length() < 6) {
                return ResponseEntity.status(400).body("Wachtwoord moet minimaal 6 tekens zijn");
            }

            user.setPassword(passwordService.hashPassword(nieuw));
            userRepository.save(user);

            return ResponseEntity.ok("Wachtwoord gewijzigd");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    private User getUserFromHeader(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));
    }
}