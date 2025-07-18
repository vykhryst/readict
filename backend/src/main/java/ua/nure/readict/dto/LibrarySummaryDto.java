package ua.nure.readict.dto;


public record LibrarySummaryDto(
        long total,
        long read,
        long reading,
        long want
) {}
