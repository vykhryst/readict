package ua.nure.readict.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UserUpdateDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        Set<Long> favouriteGenreIds       // допускаємо null
) {
}