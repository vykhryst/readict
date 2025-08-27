package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.service.interfaces.BookService;

@RestController
@AllArgsConstructor
@RequestMapping("/recommendations")
@Tag(name = "Recommendations", description = "API for retrieving book recommendations for users")
public class RecommendationController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get book recommendations", description = "Returns a paginated list of book recommendations for the current user, optionally filtered by genre and sorted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })
    public ResponseEntity<Page<BookResponse>> getRecommendations(
            @Parameter(description = "Filter by genre ID", example = "5") @RequestParam(required = false) Long genreId,
            @Parameter(description = "Sorting criteria, e.g., 'title,asc' or 'publicationDate,desc'", example = "title,asc") @RequestParam(required = false) String sort,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of books per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CurrentUser cu
    ) {
        Page<BookResponse> recs = bookService.getRecommendedBooksByUserId(
                cu.getUser(), genreId, sort, page, size);
        return ResponseEntity.ok(recs);
    }
}
