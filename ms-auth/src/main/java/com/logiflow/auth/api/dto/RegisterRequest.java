package com.logiflow.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 128) @Schema(example = "jperez") String username,
        @NotBlank @Email @Schema(example = "jperez@test.com") String email,
        @NotBlank @Size(min = 6, max = 128) @Schema(example = "secret123") String password,
        @Schema(example = "ROLE_USER", description = "Opcional. Valores: ROLE_USER, ROLE_ADMIN") String role) {}
