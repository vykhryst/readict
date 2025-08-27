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
import ua.nure.readict.dto.TropeDto;
import ua.nure.readict.service.interfaces.TropeService;

@RestController
@RequestMapping("/admin/trope")
@RequiredArgsConstructor
@Tag(name = "Admin Trope", description = "APIs for admin management of tropes")
public class AdminTropeController {

    private final TropeService tropeService;

    @PostMapping
    @Operation(
            summary = "Create a new trope",
            description = "Add a new trope to the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TropeDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<TropeDto> createTrope(
            @Parameter(description = "Details of the trope to create")
            @RequestBody @Valid TropeDto tropeDto) {
        return ResponseEntity.status(201).body(tropeService.create(tropeDto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing trope",
            description = "Update details of an existing trope by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TropeDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation failed",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\n\"field\": \"Validation error message\"\n}"))),
                    @ApiResponse(responseCode = "404", description = "Trope not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<TropeDto> updateTrope(
            @Parameter(description = "ID of the trope to update", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated details of the trope")
            @RequestBody @Valid TropeDto tropeDto) {
        return ResponseEntity.ok(tropeService.update(id, tropeDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a trope",
            description = "Remove a trope from the system by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema)),
                    @ApiResponse(responseCode = "404", description = "Trope not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteTrope(
            @Parameter(description = "ID of the trope to deleteBookFromLibrary", example = "1")
            @PathVariable Long id) {
        tropeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
