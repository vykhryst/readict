package ua.nure.readict.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordDto(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {}
