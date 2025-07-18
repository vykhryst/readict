package ua.nure.readict.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.nure.readict.entity.Rating;
import ua.nure.readict.repository.RatingRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for computing the most similar users based on mean-centered cosine similarity.
 * The code is divided into separate methods for better readability and testability.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimilarityService {

    private final RatingRepository ratingRepository;
    private final RecommenderProperties properties;

    /**
     * Main entry point: returns the top-K users most similar to the specified user.
     *
     * @param targetUserId the ID of the user for whom to find similar neighbors
     * @return an ordered map of {otherUserId -> similarityScore}, sorted descending by score
     */
    @Transactional
    public Map<Long, Double> computeSimilarities(Long targetUserId) {
        // Step 1: load all ratings and group them by user
        Map<Long, Map<Long, Integer>> allUserRatings = loadAllUserRatings();

        // Step 2: get the target user's ratings and compute their average
        Map<Long, Integer> targetRatings = allUserRatings.getOrDefault(targetUserId, Collections.emptyMap());
        double targetMean = calculateAverageRating(targetRatings);

        // Step 3: compute raw similarity values between target and all other users
        Map<Long, Double> rawSimilarities = computeRawSimilarities(
                targetUserId, targetRatings, targetMean, allUserRatings
        );

        // Step 4: filter and return the top-K most similar neighbors
        return selectTopKNeighbors(rawSimilarities);
    }

    /**
     * Loads all ratings from the database and groups them into a map of userId to (bookId -> score).
     *
     * @return a map where each key is a user ID and the value is another map of book IDs to rating scores
     */
    private Map<Long, Map<Long, Integer>> loadAllUserRatings() {
        List<Rating> ratings = ratingRepository.findAll();
        if (ratings.isEmpty()) {
            log.info("No ratings found in the database");
            return Collections.emptyMap();
        }

        Map<Long, Map<Long, Integer>> userRatingsMap = new HashMap<>();
        for (Rating rating : ratings) {
            Long userId = rating.getUserId();
            Long bookId = rating.getBookId();
            int score = rating.getScore();
            userRatingsMap
                    .computeIfAbsent(userId, id -> new HashMap<>())
                    .put(bookId, score);
        }
        return userRatingsMap;
    }

    /**
     * Calculates the average rating value for a given user's ratings.
     *
     * @param ratings a map of book IDs to rating scores for a single user
     * @return the average score, or 0.0 if the user has no ratings
     */
    private double calculateAverageRating(Map<Long, Integer> ratings) {
        return ratings.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    /**
     * Computes mean-centered cosine similarity between the target user and all other users.
     * Only considers users with at least a minimum number of common rated books.
     *
     * @param targetUserId   the ID of the target user
     * @param targetRatings  the map of book IDs to scores for the target user
     * @param targetMean     the average rating of the target user
     * @param allUserRatings the full map of all users' ratings
     * @return a map of {otherUserId -> similarityScore}
     */
    private Map<Long, Double> computeRawSimilarities(
            Long targetUserId,
            Map<Long, Integer> targetRatings,
            double targetMean,
            Map<Long, Map<Long, Integer>> allUserRatings
    ) {
        Map<Long, Double> similarities = new HashMap<>();

        for (Map.Entry<Long, Map<Long, Integer>> entry : allUserRatings.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(targetUserId)) {
                continue;
            }

            Map<Long, Integer> otherRatings = entry.getValue();

            // Identify common books rated by both users
            Set<Long> commonBookIds = new HashSet<>(targetRatings.keySet());
            commonBookIds.retainAll(otherRatings.keySet());

            // Skip if fewer common ratings than the configured minimum
            if (commonBookIds.size() < properties.getMinCommon()) {
                continue;
            }

            // Compute average for the other user
            double otherMean = calculateAverageRating(otherRatings);

            double numerator = 0.0;
            double sumSquareDiffTarget = 0.0;
            double sumSquareDiffOther = 0.0;

            // Compute components for cosine similarity
            for (Long bookId : commonBookIds) {
                double diffTarget = targetRatings.get(bookId) - targetMean;
                double diffOther = otherRatings.get(bookId) - otherMean;

                numerator += diffTarget * diffOther;
                sumSquareDiffTarget += diffTarget * diffTarget;
                sumSquareDiffOther += diffOther * diffOther;
            }

            // Avoid division by zero; similarity is zero if no variance
            double similarity = (sumSquareDiffTarget == 0 || sumSquareDiffOther == 0)
                    ? 0.0
                    : numerator / (Math.sqrt(sumSquareDiffTarget) * Math.sqrt(sumSquareDiffOther));

            if (!Double.isNaN(similarity)) {
                similarities.put(otherUserId, similarity);
            }
        }

        return similarities;
    }

    /**
     * Sorts the similarity map in descending order of scores and limits it to the top-K neighbors.
     *
     * @param rawSimilarities a map of {userId -> similarityScore}
     * @return a LinkedHashMap of the top-K entries, preserving descending order
     */
    private Map<Long, Double> selectTopKNeighbors(Map<Long, Double> rawSimilarities) {
        return rawSimilarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(properties.getKNeighbors())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }
}
