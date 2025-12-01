package com.pfa.authentification.DTO;

import com.pfa.authentification.Entity.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
}