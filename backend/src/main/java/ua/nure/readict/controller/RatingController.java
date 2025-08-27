package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.RatingRequest;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.service.interfaces.RatingService;

@RestController
@RequestMapping("/books/{bookId}/rating")
@RequiredArgsConstructor
@Tag(name = "Rating", description = "API for managing book ratings by users")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/me")
    @Operation(summary = "Get user's rating for a book", description = "Returns the current user's rating for the specified book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No rating found for this book by the current user")
    })
    public ResponseEntity<Integer> getMyRating(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @AuthenticationPrincipal CurrentUser cu) {

        Integer score = ratingService.findMyRating(cu.getUser().getId(), bookId);
        return score != null ? ResponseEntity.ok(score) : ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Rate a book", description = "Sets a rating for the specified book by the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating set successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating value")
    })
    public ResponseEntity<Void> rateBook(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @Parameter(description = "Rating request body") @RequestBody @Valid RatingRequest rq,
            @AuthenticationPrincipal CurrentUser cu) {

        ratingService.setRating(cu.getUser().getId(), bookId, rq.score());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete user's rating for a book", description = "Removes the current user's rating for the specified book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No rating found for this book by the current user")
    })
    public ResponseEntity<Void> deleteRatingForBook(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @AuthenticationPrincipal CurrentUser cu) {

        ratingService.deleteRating(cu.getUser().getId(), bookId);
        return ResponseEntity.noContent().build();
    }
}
