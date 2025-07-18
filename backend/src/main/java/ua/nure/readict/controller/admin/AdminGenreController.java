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
import ua.nure.readict.dto.ErrorResponse;
import ua.nure.readict.dto.GenreDto;
import ua.nure.readict.service.interfaces.GenreService;

@RestController
@RequestMapping("/admin/genre")
@RequiredArgsConstructor
public class AdminGenreController {

    private final GenreService genreService;

    @PostMapping
    @Operation(
            summary = "Create a new genre",
            description = "Add a new genre to the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<GenreDto> createGenre(
            @Parameter(description = "Details of the genre to create")
            @RequestBody @Valid GenreDto genreDto) {
        return ResponseEntity.status(201).body(genreService.create(genreDto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing genre",
            description = "Update details of an existing genre by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "404", description = "Genre not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<GenreDto> updateGenre(
            @Parameter(description = "ID of the genre to update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated details of the genre")
            @RequestBody @Valid GenreDto genreDto) {
        return ResponseEntity.ok(genreService.update(id, genreDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a genre",
            description = "Remove a genre from the system by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Genre not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteGenre(
            @Parameter(description = "ID of the genre to deleteBookFromLibrary", example = "1")
            @PathVariable Long id) {
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
