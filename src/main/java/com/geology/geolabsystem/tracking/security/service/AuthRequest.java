package com.geology.geolabsystem.tracking.security.service;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
