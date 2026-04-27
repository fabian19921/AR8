package com.laverman.STM1D.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String blockKey;  // unieke sleutel per blok,
    private String scoreValue; // "10:15" of "80" of "12"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Score() {}

}