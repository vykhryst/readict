package ua.nure.readict.controller;

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
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Page<ReviewDto> getAllReviewsByBook(@PathVariable Long bookId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size) {
        return reviewService.findAllByBookId(bookId, page, size);
    }

    @GetMapping("/me")
    public ResponseEntity<ReviewDto> getMyReview(@PathVariable Long bookId,
                                                 @AuthenticationPrincipal CurrentUser cu) {
        return reviewService.findReviewByUser(bookId, cu)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<Void> createOrUpdateReview(@PathVariable Long bookId,
                                                     @RequestBody @Valid CreateReviewRequest rq,
                                                     @AuthenticationPrincipal CurrentUser cu) {
        reviewService.createOrUpdateReview(bookId, rq, cu);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@PathVariable Long bookId,
                                             @AuthenticationPrincipal CurrentUser cu) {
        reviewService.deleteReview(bookId, cu);
        return ResponseEntity.noContent().build();
    }
}
