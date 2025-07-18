package ua.nure.readict.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import ua.nure.readict.dto.AuthorDto;
import ua.nure.readict.dto.GenreDto;
import ua.nure.readict.dto.SeriesDto;
import ua.nure.readict.dto.TropeDto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link ua.nure.readict.entity.Book}
 */
@Schema(description = "Response DTO for book details")
public record BookResponse(

        @Schema(description = "ID of the book", example = "1")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @Schema(description = "Title of the book", example = "Harry Potter and the Philosopher's Stone")
        String title,

        @Schema(description = "Details of the series the book belongs to")
        SeriesDto series,

        @Schema(description = "Number of the book in the series", example = "1")
        Integer seriesNumber,

        @Schema(description = "Details of the author of the book")
        AuthorDto author,

        @Schema(description = "Annotation or getLibrarySummary of the book", example = "A young boy discovers he is a wizard...")
        String annotation,

        @Schema(description = "Number of pages in the book", example = "320")
        Integer pageCount,

        @Schema(description = "Date the book was published", example = "1997-06-26")
        LocalDate publicationDate,

        @Schema(description = "ISBN of the book", example = "978-0747532699")
        String isbn,

        @Schema(description = "Language of the book", example = "English")
        String language,

        @Schema(description = "Average rating of the book", example = "4.5")
        Double averageRating,

        @Schema(description = "Publisher of the book", example = "Bloomsbury")
        String publisher,

        @Schema(description = "Edition number of the book", example = "2")
        Integer edition,

        @Schema(description = "Number of reviews for the book", example = "500")
        Integer reviewCount,

        @Schema(description = "Number of ratings for the book", example = "10000")
        Integer ratingCount,

        @Schema(description = "Timestamp when the book was created", example = "2024-01-01T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Genres associated with the book")
        Set<GenreDto> genres,

        @Schema(description = "Tropes associated with the book")
        Set<TropeDto> tropes,

        @Schema(description = "URL of the book cover image", example = "https://example.com/cover.jpg")
        String cover

) implements Serializable {
}
