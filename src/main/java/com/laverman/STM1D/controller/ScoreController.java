package com.laverman.STM1D.controller;

import com.laverman.STM1D.model.Score;
import com.laverman.STM1D.model.User;
import com.laverman.STM1D.repository.ScoreRepository;
import com.laverman.STM1D.repository.UserRepository;
import com.laverman.STM1D.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ScoreController(ScoreRepository scoreRepository,
                           UserRepository userRepository,
                           JwtService jwtService) {
        this.scoreRepository = scoreRepository;
        this.userRepository  = userRepository;
        this.jwtService      = jwtService;
    }

    // Score opslaan of updaten
    @PostMapping
    public ResponseEntity<?> saveScore(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody Map<String, String> body) {
        try {
            User user = getUserFromHeader(authHeader);
            LocalDate date = LocalDate.parse(body.get("date"));
            String blockKey   = body.get("blockKey");
            String scoreValue = body.get("scoreValue");

            // Bestaande score updaten of nieuwe aanmaken
            Score score = scoreRepository
                    .findByUserAndDateAndBlockKey(user, date, blockKey)
                    .orElse(new Score());

            score.setUser(user);
            score.setDate(date);
            score.setBlockKey(blockKey);
            score.setScoreValue(scoreValue);

            scoreRepository.save(score);
            return ResponseEntity.ok("Score opgeslagen");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // Alle scores van ingelogde gebruiker voor een dag ophalen
    @GetMapping("/{date}")
    public ResponseEntity<?> getScores(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable String date) {
        try {
            User user = getUserFromHeader(authHeader);
            LocalDate localDate = LocalDate.parse(date);

            List<Score> scores = scoreRepository.findByUserAndDate(user, localDate);

            Map<String, String> result = new HashMap<>();
            scores.forEach(s -> result.put(s.getBlockKey(), s.getScoreValue()));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // Leaderboard voor een specifiek blok op een dag
    @GetMapping("/{date}/{blockKey}/leaderboard")
    public ResponseEntity<?> getLeaderboard(@PathVariable String date,
                                            @PathVariable String blockKey) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Score> scores = scoreRepository.findByDateAndBlockKey(localDate, blockKey);

            List<Map<String, String>> result = scores.stream()
                    .map(s -> Map.of(
                            "name",       s.getUser().getName(),
                            "scoreValue", s.getScoreValue(),
                            "blockKey",   s.getBlockKey()
                    ))
                    .toList();

            return ResponseEntity.ok(result);
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