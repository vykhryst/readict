package ua.nure.readict.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record LibraryBookDto(
        Long   id,
        String title,
        String cover,
        String author,
        Long authorId,
        Double myRating,
        LocalDateTime addedAt,
        String shelf,               // READ, CURRENTLY_READING, WANT_TO_READ
        String review,       // перші 200 символів або null
        LocalDate publicationDate,
        Set<Long> genreIds
) {}