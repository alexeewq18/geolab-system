package com.geology.geolabsystem.tracking.security.controller;

import com.geology.geolabsystem.tracking.security.dto.JwtAuthenticationResponse;
import com.geology.geolabsystem.tracking.security.dto.SignInRequest;
import com.geology.geolabsystem.tracking.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
