package com.laverman.STM1D.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "trainingsplannen")
public class TrainingsPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String warmup;

    @Column(columnDefinition = "TEXT")
    private String cooling;

    @Column(columnDefinition = "TEXT")
    private String blocksJson;

    public TrainingsPlan() {}

}