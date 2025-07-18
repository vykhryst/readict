package ua.nure.readict.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link ua.nure.readict.entity.Book}
 */
@Schema(description = "Request DTO for creating or updating a book")
public record BookRequest(

        @NotBlank(message = "Title is mandatory")
        @Schema(description = "Title of the book", example = "Harry Potter and the Philosopher's Stone")
        String title,

        @Schema(description = "ID of the series the book belongs to (optional)", example = "1")
        Long seriesId,

        @Schema(description = "Number of the book in the series (optional)", example = "1")
        Integer seriesNumber,

        @NotNull(message = "Author ID is mandatory")
        @Schema(description = "ID of the author", example = "1")
        Long authorId,

        @NotBlank(message = "Annotation is mandatory")
        @Schema(description = "Annotation or getLibrarySummary of the book", example = "A young boy discovers he is a wizard...")
        String annotation,

        @NotNull(message = "Page count is mandatory")
        @Positive(message = "Page count must be greater than 0")
        @Schema(description = "Number of pages in the book", example = "320")
        Integer pageCount,

        @NotNull(message = "Publication date is mandatory")
        @PastOrPresent(message = "Publication date cannot be in the future")
        @Schema(description = "Date the book was published", example = "1997-06-26")
        LocalDate publicationDate,

        @NotBlank(message = "ISBN is mandatory")
        @Size(max = 20, message = "ISBN must be at most 20 characters")
        @Schema(description = "ISBN of the book", example = "978-0747532699")
        String isbn,

        @NotBlank(message = "Language is mandatory")
        @Size(max = 50, message = "Language must be at most 50 characters")
        @Schema(description = "Language of the book", example = "English")
        String language,

        @Size(max = 255, message = "Publisher name must be at most 255 characters")
        @Schema(description = "Publisher of the book (optional)", example = "Bloomsbury")
        String publisher,

        @PositiveOrZero(message = "Edition must be 0 or greater")
        @Schema(description = "Edition number of the book (optional)", example = "2")
        Integer edition,

        @NotNull(message = "Genre IDs are mandatory")
        @NotEmpty(message = "At least one genre must be specified")
        @Size(min = 1, message = "At least one genre must be specified")
        @Schema(description = "IDs of genres associated with the book", example = "[1, 2]")
        Set<Long> genreIds,

        @NotNull(message = "Trope IDs are mandatory")
        @NotEmpty(message = "At least one trope must be specified")
        @Schema(description = "IDs of tropes associated with the book", example = "[1, 3]")
        Set<Long> tropeIds

) implements Serializable {
}
