package ua.nure.readict.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.nure.readict.entity.Rating;
import ua.nure.readict.recommendation.RecommendationService;
import ua.nure.readict.repository.RatingRepository;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scheduler that runs a daily task to refresh recommendations for all users
 * who have at least one rating.
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RecommendationScheduler {

    private final RecommendationService recommendationService;
    private final RatingRepository ratingRepository;

    /**
     * Scheduled job executed every day at 3:00 AM server time.
     * Retrieves all user IDs who have submitted at least one rating,
     * and refreshes their recommendations.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void computeAllUsersRecommendations() {
        log.info("=== Starting scheduled recommendations computation for all users ===");

        Set<Long> userIds = ratingRepository.findAll().stream()
                .map(Rating::getUserId)
                .collect(Collectors.toSet());

        for (Long userId : userIds) {
            recommendationService.refreshRecommendationsForUser(userId);
        }

        log.info("=== Completed scheduled recommendations computation for users ===");
    }
}
