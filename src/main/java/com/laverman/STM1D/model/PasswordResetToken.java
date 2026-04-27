package com.laverman.STM1D.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime expiresAt;
    private boolean used;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken() {}

}