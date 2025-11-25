package com.pfa.authentification.controller;

import com.pfa.authentification.config.JwtUtils;
import com.pfa.authentification.entity.User;
import com.pfa.authentification.dto.LoginRequest;
import com.pfa.authentification.dto.AuthResponse;
import com.pfa.authentification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Username déjà utilisé"));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Email déjà utilisé"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(savedUser.getUsername(), savedUser.getRole().toString());

        return ResponseEntity.ok(new AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getRole().toString(),
                "Inscription réussie"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Utilisateur introuvable"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, null, "Mot de passe incorrect"));
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().toString());

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().toString(),
                "Connexion réussie"
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        if (jwtUtils.validateToken(token)) {
            String username = jwtUtils.extractUsername(token);
            return ResponseEntity.ok("Token valide pour " + username);
        }
        return ResponseEntity.badRequest().body("Token invalide");
    }
    @GetMapping("/user")
    public User getUserById(@RequestParam Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
