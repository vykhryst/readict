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
import ua.nure.readict.dto.SeriesDto;
import ua.nure.readict.service.interfaces.SeriesService;

@RestController
@RequestMapping("/admin/series")
@RequiredArgsConstructor
@Tag(name = "Admin Series", description = "APIs for admin management of book series")
public class AdminSeriesController {

    private final SeriesService seriesService;

    @PostMapping
    @Operation(
            summary = "Create a new series",
            description = "Add a new book series to the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SeriesDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<SeriesDto> createSeries(
            @Parameter(description = "Details of the series to create")
            @RequestBody @Valid SeriesDto seriesDto) {
        return ResponseEntity.status(201).body(seriesService.create(seriesDto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing series",
            description = "Update details of an existing series by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SeriesDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "404", description = "Series not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<SeriesDto> updateSeries(
            @Parameter(description = "ID of the series to update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated details of the series")
            @RequestBody @Valid SeriesDto seriesDto) {
        return ResponseEntity.ok(seriesService.update(id, seriesDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a series",
            description = "Remove a book series from the system by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema)),
                    @ApiResponse(responseCode = "404", description = "Series not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteSeries(
            @Parameter(description = "ID of the series to deleteBookFromLibrary", example = "1")
            @PathVariable Long id) {
        seriesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
