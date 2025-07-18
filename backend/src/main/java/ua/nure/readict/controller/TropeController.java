package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.nure.readict.dto.ErrorResponse;
import ua.nure.readict.dto.TropeDto;
import ua.nure.readict.service.interfaces.TropeService;

import java.util.List;

@RestController
@RequestMapping("/tropes")
@Tag(name = "Tropes")
@RequiredArgsConstructor
public class TropeController {

    private final TropeService tropeService;

    @GetMapping
    @Operation(
            summary = "Retrieve all tropes",
            description = "Get a getAllReviewsByBook of all tropes.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TropeDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<List<TropeDto>> getAllTropes() {
        return ResponseEntity.ok(tropeService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a trope by ID",
            description = "Get detailed information about a specific trope by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TropeDto.class))),
                    @ApiResponse(responseCode = "404", description = "Trope not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<TropeDto> getTropeById(
            @Parameter(description = "ID of the trope to retrieve", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(tropeService.getById(id));
    }
}
