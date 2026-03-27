package com.geology.geolabsystem.tracking.security.dto;

import jakarta.validation.constraints.NotBlank;


public record SignInRequest(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String username,
        @NotBlank(message = "Пароль не может быть пустым")
        String password) {
}
