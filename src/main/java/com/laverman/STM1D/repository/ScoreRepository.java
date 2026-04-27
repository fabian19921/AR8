package com.laverman.STM1D.repository;

import com.laverman.STM1D.model.Score;
import com.laverman.STM1D.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    // Eigen score van gebruiker voor een blok op een dag
    Optional<Score> findByUserAndDateAndBlockKey(User user, LocalDate date, String blockKey);

    // Alle scores van een gebruiker op een dag
    List<Score> findByUserAndDate(User user, LocalDate date);

    // Alle scores voor een blok op een dag (voor leaderboard later)
    List<Score> findByDateAndBlockKey(LocalDate date, String blockKey);
}