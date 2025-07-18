package ua.nure.readict.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.ErrorResponse;
import ua.nure.readict.dto.book.BookRequest;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.service.interfaces.BookService;

@RestController
@RequestMapping("/admin/book")
@RequiredArgsConstructor
@Tag(name = "Admin Books", description = "API for managing books by admin")
public class AdminBookController {

    private final BookService bookService;

    @PostMapping
    @Operation(
            summary = "Create a new book",
            description = "Add a new book to the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "404", description = "Entity not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<BookResponse> createBook(
            @Parameter(description = "Details of the book to create")
            @RequestBody @Valid BookRequest bookRequest) {
        return ResponseEntity.status(201).body(bookService.create(bookRequest));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing book",
            description = "Update details of an existing book by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "404", description = "Entity not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "ID of the book to update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated details of the book")
            @RequestBody @Valid BookRequest bookRequest) {
        return ResponseEntity.ok(bookService.update(id, bookRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a book",
            description = "Remove a book from the system by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema)),
                    @ApiResponse(responseCode = "404", description = "Book not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to deleteBookFromLibrary", example = "1")
            @PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
