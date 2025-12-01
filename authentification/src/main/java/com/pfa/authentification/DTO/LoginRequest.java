package com.pfa.authentification.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
