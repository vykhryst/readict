package ua.nure.readict.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.AuthorDto;
import ua.nure.readict.dto.ErrorResponse;
import ua.nure.readict.service.interfaces.AuthorService;

@RestController
@RequestMapping("/admin/author")
@RequiredArgsConstructor
public class AdminAuthorController {

    private final AuthorService authorService;

    @PostMapping
    @Operation(
            summary = "Create a new author",
            description = "Add a new author to the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<AuthorDto> createAuthor(
            @Parameter(description = "Details of the author to create")
            @RequestBody @Valid AuthorDto authorDto) {
        return ResponseEntity.status(201).body(authorService.create(authorDto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing author",
            description = "Update details of an existing author by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "404", description = "Author not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"message\": \"Entity with id X not found\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<AuthorDto> updateAuthor(
            @Parameter(description = "ID of the author to update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated details of the author")
            @RequestBody @Valid AuthorDto authorDto) {
        return ResponseEntity.ok(authorService.update(id, authorDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an author",
            description = "Remove an author from the system by their ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema)),
                    @ApiResponse(responseCode = "404", description = "Author not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"message\": \"Entity with id X not found\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteAuthor(
            @Parameter(description = "ID of the author to deleteBookFromLibrary", example = "1")
            @PathVariable Long id) {
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
