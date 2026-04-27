package com.laverman.STM1D.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "personal_records")
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exercise;  // bijv. "backsquat"
    private Double weight;    // in kg

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PersonalRecord() {}

}