package ua.nure.readict.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ua.nure.readict.event.FavouriteGenresChangedEvent;
import ua.nure.readict.event.RatingChangedEvent;
import ua.nure.readict.recommendation.RecommendationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationEventsListener {

    private final RecommendationService recommendationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRatingChanged(RatingChangedEvent e) {
        refreshRecommendationsForUser(e.userId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGenresChanged(FavouriteGenresChangedEvent e) {
        refreshRecommendationsForUser(e.userId());
    }

    private void refreshRecommendationsForUser(Long userId) {
        log.info("Re‑calculating recommendations for user {}", userId);
        recommendationService.refreshRecommendationsForUser(userId);
        log.info("Recommendations for user {} have been re‑calculated", userId);
    }
}