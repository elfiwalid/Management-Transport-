package com.pfa.authentification.controller;

import com.pfa.authentification.entity.User;
import com.pfa.authentification.entity.Role;
import com.pfa.authentification.dto.LoginRequest;
import com.pfa.authentification.dto.AuthResponse;
import com.pfa.authentification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ Ajouter

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, null, "Username déjà utilisé"));
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, null, "Email déjà utilisé"));
            }

            // ✅ UTILISER BCrypt au lieu de "hashed_"
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userRepository.save(user);
            String token = "token_" + UUID.randomUUID().toString();

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    savedUser.getUsername(),
                    savedUser.getRole().toString(),
                    "Inscription réussie"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Erreur lors de l'inscription"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, null, "Utilisateur introuvable"));
            }

            User user = userOpt.get();

            // ✅ UTILISER BCrypt pour vérifier le mot de passe
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, null, "Mot de passe incorrect"));
            }

            String token = "token_" + UUID.randomUUID().toString();

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getRole().toString(),
                    "Connexion réussie"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Erreur lors de la connexion"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        // Validation simple du token (en production, valider le JWT)
        if (token != null && token.startsWith("token_")) {
            return ResponseEntity.ok("Token valide");
        }
        return ResponseEntity.badRequest().body("Token invalide");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // En production, invalider le token
        return ResponseEntity.ok("Déconnexion réussie");
    }

    @GetMapping("/users")
    public ResponseEntity<java.util.List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service Authentification is running");
    }
}