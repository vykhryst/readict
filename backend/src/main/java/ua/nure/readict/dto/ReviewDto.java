package ua.nure.readict.dto;

import java.time.LocalDateTime;

public record ReviewDto(
        Long userId,
        String userName,
        Integer rating,
        String content,
        LocalDateTime addedAt) {}