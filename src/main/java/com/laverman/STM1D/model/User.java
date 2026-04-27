package com.laverman.STM1D.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naam is verplicht")
    private String name;

    @NotBlank(message = "Emailadres is verplicht")
    @Email(message = "Geldig emailadres vereist")
    private String email;

    @NotBlank(message = "Wachtwoord is verplicht")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String birthDate;
    private String location;
    private String quote;
}