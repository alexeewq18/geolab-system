package com.geology.geolabsystem.tracking.security.service;

import com.geology.geolabsystem.tracking.security.dto.JwtAuthenticationResponse;
import com.geology.geolabsystem.tracking.security.dto.SignInRequest;
import com.geology.geolabsystem.tracking.security.entity.RefreshToken;
import com.geology.geolabsystem.tracking.security.entity.UserEntity;
import com.geology.geolabsystem.tracking.security.entity.enums.Role;
import com.geology.geolabsystem.tracking.security.repository.RefreshTokenRepository;
import com.geology.geolabsystem.tracking.security.repository.UserRepository;
import com.geology.geolabsystem.tracking.security.service.token.JwtService;
import com.geology.geolabsystem.tracking.security.service.token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JwtAuthenticationResponse login(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return buildResponse(accessToken, refreshToken.getToken(), user.getId(),
                user.getUsername(), user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.joining(",")));
    }

    public JwtAuthenticationResponse refresh(String rawRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token не найден"));

        refreshTokenService.verifyExpiration(refreshToken);

        UserEntity user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);

        return buildResponse(newAccessToken, rawRefreshToken, user.getId(),
                user.getUsername(), user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.joining(",")));
    }

    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByToken(rawRefreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    private JwtAuthenticationResponse buildResponse
            (String accessToken, String refreshToken,
             Long userId, String username, String roles) {
        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .username(username)
                .role(roles)
                .build();
    }
}

