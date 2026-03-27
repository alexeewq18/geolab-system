package com.geology.geolabsystem.tracking.security.dto;

import lombok.Builder;

@Builder
public record JwtAuthenticationResponse(
        String accessToken,
        String refreshToken,

        Long userId,
        String username,
        String role
) {
}
