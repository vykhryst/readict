package ua.nure.readict.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReviewRequest(@NotBlank String content) {
}
