package com.pfa.authentification.Entity;


import jakarta.persistence.*;
import lombok.*;
import com.pfa.authentification.Entity.UserRole;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifiant de connexion (unique)
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    // Nom complet (pour l'affichage dans l'UI)
    @Column(name = "full_name", length = 100)
    private String fullName;

    // Email (optionnel mais pratique)
    @Column(name = "email", length = 100)
    private String email;

    // Mot de passe HASHÉ (BCrypt) - jamais en clair !
    @Column(name = "password", nullable = false, length = 200)
    private String password;

    // Rôle métier : ADMIN / OPERATOR / DRIVER / VIEWER
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;


}
