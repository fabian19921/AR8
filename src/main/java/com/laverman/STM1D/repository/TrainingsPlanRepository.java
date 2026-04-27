package com.laverman.STM1D.repository;

import com.laverman.STM1D.model.TrainingsPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingsPlanRepository extends JpaRepository<TrainingsPlan, Long> {
    Optional<TrainingsPlan> findByDate(LocalDate date);
    List<TrainingsPlan> findAllByDateBetween(LocalDate start, LocalDate end);
}