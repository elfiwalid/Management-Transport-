package com.pfa.authentification.Service;


import com.pfa.authentification.DTO.AuthResponse;
import com.pfa.authentification.DTO.LoginRequest;
import com.pfa.authentification.DTO.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse me(String token);
}