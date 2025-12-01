package com.pfa.authentification.Controller;


import com.pfa.authentification.DTO.AuthResponse;
import com.pfa.authentification.DTO.LoginRequest;
import com.pfa.authentification.DTO.RegisterRequest;
import com.pfa.authentification.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin // si besoin d'appeler depuis front
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@RequestHeader("Authorization") String authHeader) {
        AuthResponse response = authService.me(authHeader);
        return ResponseEntity.ok(response);
    }
}
