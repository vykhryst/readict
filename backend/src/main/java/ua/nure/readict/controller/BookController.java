package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.ErrorResponse;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.service.interfaces.BookService;

import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "API for managing books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(
            summary = "Retrieve all books",
            description = "Get a paginated and sorted getAllReviewsByBook of books with optional search by title and filtering by genre.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n  \"parameterName\": \"error\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @Parameter(description = "Search by book title", example = "Harry Potter")
            @RequestParam(required = false) String title,
            @Parameter(description = "Filter by genre IDs", example = "1,2,3")
            @RequestParam(required = false) List<Long> genreIds,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of books per findAllInUserLibrary", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sorting criteria, e.g., 'title,asc' or 'publicationDate,desc'", example = "title,asc")
            @RequestParam(defaultValue = "title,asc") String sort
    ) {
        return ResponseEntity.ok(bookService.getAll(title, genreIds, page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a book by ID",
            description = "Get detailed information about a specific book by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Book not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"message\": \"Entity with id X not found\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "ID of the book to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }


}
