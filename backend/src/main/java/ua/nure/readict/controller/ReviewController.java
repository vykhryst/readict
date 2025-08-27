package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.CreateReviewRequest;
import ua.nure.readict.dto.ReviewDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.service.interfaces.ReviewService;

@RestController
@RequestMapping("/books/{bookId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "API for managing book reviews by users")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get all reviews for a book", description = "Returns a paginated list of all reviews for the specified book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    })
    public Page<ReviewDto> getAllReviewsByBook(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of reviews per page", example = "5") @RequestParam(defaultValue = "5") int size) {

        return reviewService.findAllByBookId(bookId, page, size);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my review for a book", description = "Returns the current user's review for the specified book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No review found for this book by the current user")
    })
    public ResponseEntity<ReviewDto> getMyReview(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @AuthenticationPrincipal CurrentUser cu) {

        return reviewService.findReviewByUser(bookId, cu)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    @Operation(summary = "Create or update review", description = "Creates a new review or updates the existing review for the current user on the specified book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review created or updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review data")
    })
    public ResponseEntity<Void> createOrUpdateReview(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @Parameter(description = "Review request body") @RequestBody @Valid CreateReviewRequest rq,
            @AuthenticationPrincipal CurrentUser cu) {

        reviewService.createOrUpdateReview(bookId, rq, cu);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete my review for a book", description = "Deletes the current user's review for the specified book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "No review found for this book by the current user")
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @AuthenticationPrincipal CurrentUser cu) {

        reviewService.deleteReview(bookId, cu);
        return ResponseEntity.noContent().build();
    }
}
