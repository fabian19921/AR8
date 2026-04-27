package com.laverman.STM1D.controller;

import com.laverman.STM1D.model.PersonalRecord;
import com.laverman.STM1D.model.User;
import com.laverman.STM1D.repository.PersonalRecordRepository;
import com.laverman.STM1D.repository.UserRepository;
import com.laverman.STM1D.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pr")
public class PersonalRecordController {

    private final PersonalRecordRepository prRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public PersonalRecordController(PersonalRecordRepository prRepository,
                                    UserRepository userRepository,
                                    JwtService jwtService) {
        this.prRepository  = prRepository;
        this.userRepository = userRepository;
        this.jwtService    = jwtService;
    }

    // Alle PR's van ingelogde gebruiker ophalen
    @GetMapping
    public ResponseEntity<?> getAllPRs(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = getUserFromHeader(authHeader);
            List<PersonalRecord> prs = prRepository.findByUser(user);

            Map<String, Double> result = prs.stream()
                    .collect(Collectors.toMap(
                            PersonalRecord::getExercise,
                            PersonalRecord::getWeight
                    ));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // PR opslaan of updaten
    @PostMapping
    public ResponseEntity<?> savePR(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody Map<String, Object> body) {
        try {
            User user     = getUserFromHeader(authHeader);
            String exercise = ((String) body.get("exercise"))
                    .toLowerCase().trim().replace(" ", "");
            Double weight   = Double.parseDouble(body.get("weight").toString());

            Optional<PersonalRecord> existing =
                    prRepository.findByUserAndExercise(user, exercise);

            boolean isNewPR = false;

            PersonalRecord pr = existing.orElse(new PersonalRecord());

            if (existing.isEmpty() || weight > pr.getWeight()) {
                isNewPR = true;
            }

            pr.setUser(user);
            pr.setExercise(exercise);
            pr.setWeight(weight);
            prRepository.save(pr);

            return ResponseEntity.ok(Map.of(
                    "saved",   true,
                    "isNewPR", isNewPR,
                    "weight",  weight,
                    "exercise", exercise
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // PR ophalen voor één oefening (voor percentage berekening)
    @GetMapping("/{exercise}")
    public ResponseEntity<?> getPR(@RequestHeader("Authorization") String authHeader,
                                   @PathVariable String exercise) {
        try {
            User user = getUserFromHeader(authHeader);
            String normalizedExercise = exercise.toLowerCase().trim().replace(" ", "");

            Optional<PersonalRecord> pr =
                    prRepository.findByUserAndExercise(user, normalizedExercise);

            if (pr.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(Map.of(
                    "exercise", pr.get().getExercise(),
                    "weight",   pr.get().getWeight()
            ));
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