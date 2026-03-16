package com.geology.geolabsystem.tracking.security.service;

import com.geology.geolabsystem.tracking.security.entity.RefreshToken;
import com.geology.geolabsystem.tracking.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse login(AuthRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String access = jwtService.generateAccessToken(auth);
        String refresh = jwtService.generateRefreshToken(auth);

        saveRefreshToken(refresh, auth.getName());

        return new AuthResponse(access, refresh);
    }

    public AuthResponse refresh(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid refresh token");
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                token.getUsername(), null, List.of()
        );

        String newAccess = jwtService.generateAccessToken(auth);

        return new AuthResponse(newAccess, refreshToken);
    }

    private void saveRefreshToken(String token, String username) {
        RefreshToken refresh = new RefreshToken();
        refresh.setToken(token);
        refresh.setUsername(username);
        refresh.setExpiryDate(LocalDateTime.now().plusDays(7));
        refresh.setRevoked(false);

        refreshTokenRepository.save(refresh);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });
    }
}
