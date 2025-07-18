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
import ua.nure.readict.dto.AuthorDto;
import ua.nure.readict.dto.ErrorResponse;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.service.interfaces.AuthorService;

import java.util.Map;

@RestController
@RequestMapping("/authors")
@Tag(name = "Authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;


    @GetMapping
    @Operation(
            summary = "Retrieve all authors",
            description = "Get a paginated getAllReviewsByBook of authors with optional search by name and sorting.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Page<AuthorDto>> getAllAuthors(
            @Parameter(description = "Search by any part of the author's name", example = "George")
            @RequestParam(required = false) String name,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of authors per findAllInUserLibrary", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sorting criteria, e.g., 'firstName,asc' or 'lastName,desc'", example = "firstName,asc")
            @RequestParam(defaultValue = "firstName,asc") String sort
    ) {
        return ResponseEntity.ok(authorService.getAll(name, page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve an author by ID",
            description = "Get detailed information about a specific author by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Author not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<AuthorDto> getAuthorById(
            @Parameter(description = "ID of the author to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

    @GetMapping("/{id}/stats")
    @Operation(
            summary = "Get statistics about the author's books",
            description = "Returns number of books and average rating of the author",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{ \"bookCount\": 6, \"averageRating\": 4.7 }"))),
                    @ApiResponse(responseCode = "404", description = "Author not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Map<String, Object>> getAuthorStats(@PathVariable Long id) {
        Map<String, Object> stats = authorService.getAuthorStats(id);
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/{id}/books")
    @Operation(
            summary = "Get books by author",
            description = "Returns paginated getLibraryBookList of books by specific author, sorted by rating and series",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "404", description = "Author not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Page<BookResponse>> getBooksByAuthor(
            @Parameter(description = "ID of the author", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of books per page", example = "12")
            @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "Sorting criteria", example = "averageRating,desc")
            @RequestParam(defaultValue = "series.name,asc,seriesNumber,asc,averageRating,desc") String sort
    ) {
        return ResponseEntity.ok(authorService.getBooksByAuthor(id, page, size, sort));
    }

}

