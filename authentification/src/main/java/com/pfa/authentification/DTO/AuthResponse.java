package com.pfa.authentification.DTO;

import com.pfa.authentification.Entity.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
}
