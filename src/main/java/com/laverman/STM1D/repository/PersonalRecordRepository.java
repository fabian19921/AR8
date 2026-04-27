package com.laverman.STM1D.repository;

import com.laverman.STM1D.model.PersonalRecord;
import com.laverman.STM1D.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Long> {
    List<PersonalRecord> findByUser(User user);
    Optional<PersonalRecord> findByUserAndExercise(User user, String exercise);
}