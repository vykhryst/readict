package ua.nure.readict.controller;

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
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/me")
    public ResponseEntity<Integer> getMyRating(@PathVariable Long bookId,
                                               @AuthenticationPrincipal CurrentUser cu) {
        Integer score = ratingService.findMyRating(cu.getUser().getId(), bookId);
        return score != null ? ResponseEntity.ok(score)
                : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> rateBook(@PathVariable Long bookId,
                                         @RequestBody @Valid RatingRequest rq,
                                         @AuthenticationPrincipal CurrentUser cu) {
        ratingService.setRating(cu.getUser().getId(), bookId, rq.score());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRatingForBook(@PathVariable Long bookId,
                                                    @AuthenticationPrincipal CurrentUser cu) {
        ratingService.deleteRating(cu.getUser().getId(), bookId);
        return ResponseEntity.noContent().build();
    }
}