package com.laverman.STM1D.controller;

import tools.jackson.databind.ObjectMapper;
import com.laverman.STM1D.model.Role;
import com.laverman.STM1D.model.TrainingsPlan;
import com.laverman.STM1D.model.User;
import com.laverman.STM1D.repository.TrainingsPlanRepository;
import com.laverman.STM1D.repository.UserRepository;
import com.laverman.STM1D.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainingsplan")
public class TrainingsPlanController {

    private final TrainingsPlanRepository repository;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TrainingsPlanController(TrainingsPlanRepository repository,
                                   ObjectMapper objectMapper,
                                   JwtService jwtService,
                                   UserRepository userRepository) {
        this.repository      = repository;
        this.objectMapper    = objectMapper;
        this.jwtService      = jwtService;
        this.userRepository  = userRepository;
    }

    // Opslaan of updaten — alleen ADMIN
    @PostMapping
    public ResponseEntity<?> save(@RequestHeader("Authorization") String authHeader,
                                  @RequestBody Map<String, Object> body) {
        try {
            checkAdmin(authHeader);

            String dateStr = (String) body.get("date");
            LocalDate date = LocalDate.parse(dateStr);

            String warmup  = (String) body.getOrDefault("warmup", "");
            String cooling = (String) body.getOrDefault("cooling", "");
            Object blocks  = body.get("blocks");

            String blocksJson = objectMapper.writeValueAsString(blocks);

            TrainingsPlan plan = repository.findByDate(date)
                    .orElse(new TrainingsPlan());

            plan.setDate(date);
            plan.setWarmup(warmup);
            plan.setCooling(cooling);
            plan.setBlocksJson(blocksJson);

            repository.save(plan);

            return ResponseEntity.ok("Opgeslagen");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Geen toegang")) {
                return ResponseEntity.status(403).body("Geen toegang");
            }
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // Verwijderen — alleen ADMIN
    @DeleteMapping("/{date}")
    public ResponseEntity<?> deleteByDate(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable String date) {
        try {
            checkAdmin(authHeader);

            LocalDate localDate = LocalDate.parse(date);
            Optional<TrainingsPlan> plan = repository.findByDate(localDate);

            if (plan.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            repository.delete(plan.get());
            return ResponseEntity.ok("Verwijderd");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Geen toegang")) {
                return ResponseEntity.status(403).body("Geen toegang");
            }
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // Ophalen per datum — voor iedereen
    @GetMapping("/{date}")
    public ResponseEntity<?> getByDate(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            Optional<TrainingsPlan> plan = repository.findByDate(localDate);

            if (plan.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TrainingsPlan p = plan.get();
            Object blocks = objectMapper.readValue(p.getBlocksJson(), Object.class);

            return ResponseEntity.ok(Map.of(
                    "date",    p.getDate().toString(),
                    "warmup",  p.getWarmup(),
                    "cooling", p.getCooling(),
                    "blocks",  blocks
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout: " + e.getMessage());
        }
    }

    // Alle datums ophalen — voor iedereen
    @GetMapping("/datums")
    public ResponseEntity<?> getAllDates() {
        List<LocalDate> dates = repository.findAll()
                .stream()
                .map(TrainingsPlan::getDate)
                .sorted()
                .toList();

        return ResponseEntity.ok(dates);
    }

    // ─── Helpers ─────────────────────────────────────
    private User getUserFromHeader(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));
    }

    private void checkAdmin(String authHeader) {
        User user = getUserFromHeader(authHeader);
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Geen toegang");
        }
    }
}