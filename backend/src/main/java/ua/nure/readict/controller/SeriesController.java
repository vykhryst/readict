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
import ua.nure.readict.dto.SeriesDto;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.service.interfaces.SeriesService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/series")
@Tag(name = "Series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping
    @Operation(
            summary = "Retrieve all series",
            description = "Get a getAllReviewsByBook of all book series.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SeriesDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<List<SeriesDto>> getAllSeries() {
        return ResponseEntity.ok(seriesService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a series by ID",
            description = "Get detailed information about a specific series by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SeriesDto.class))),
                    @ApiResponse(responseCode = "404", description = "Series not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<SeriesDto> getSeriesById(
            @Parameter(description = "ID of the series to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(seriesService.getById(id));
    }

    @GetMapping("/{id}/stats")
    @Operation(
            summary = "Get statistics about the series",
            description = "Returns number of books and average rating of the series",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{ \"bookCount\": 7, \"averageRating\": 4.8 }"))),
                    @ApiResponse(responseCode = "404", description = "Series not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Map<String, Object>> getSeriesStats(@PathVariable Long id) {
        Map<String, Object> stats = seriesService.getSeriesStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}/books")
    @Operation(
            summary = "Get books in series",
            description = "Returns paginated getAllReviewsByBook of books in specific series, sorted by series number by default",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "404", description = "Series not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Page<BookResponse>> getBooksBySeries(
            @Parameter(description = "ID of the series", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of books per findAllInUserLibrary", example = "12")
            @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "Sorting criteria", example = "seriesNumber,asc")
            @RequestParam(defaultValue = "seriesNumber,asc,averageRating,desc") String sort
    ) {
        return ResponseEntity.ok(seriesService.getBooksBySeries(id, page, size, sort));
    }

}
