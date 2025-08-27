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
import ua.nure.readict.dto.GenreDto;
import ua.nure.readict.service.interfaces.GenreService;

@RestController
@RequestMapping("/genres")
@Tag(name = "Genres", description = "APIs for managing book genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    @Operation(
            summary = "Retrieve all genres",
            description = "Get a paginated and sorted getAllReviewsByBook of genres with optional search by name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\n  \"parameterName\": \"error\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Page<GenreDto>> getAllGenres(
            @Parameter(description = "Search by genre name", example = "Fantasy")
            @RequestParam(required = false) String name,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of genres per findAllInUserLibrary", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sorting criteria, e.g., 'name,asc'", example = "name,asc")
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        return ResponseEntity.ok(genreService.getAll(name, page, size, sort));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a genre by ID",
            description = "Get detailed information about a specific genre by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenreDto.class))),
                    @ApiResponse(responseCode = "404", description = "Genre not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<GenreDto> getGenreById(
            @Parameter(description = "ID of the genre to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(genreService.getById(id));
    }

}
