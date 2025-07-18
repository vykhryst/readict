package ua.nure.readict.recommendation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.nure.readict.entity.Book;
import ua.nure.readict.entity.Genre;
import ua.nure.readict.entity.Rating;
import ua.nure.readict.entity.Recommendation;
import ua.nure.readict.repository.BookRepository;
import ua.nure.readict.repository.RatingRepository;
import ua.nure.readict.repository.RecommendationRepository;
import ua.nure.readict.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating and persisting book recommendations per user.
 * Integrates collaborative filtering and genre-based weighting into a hybrid score.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RatingRepository ratingRepository;
    private final RecommendationRepository recommendationRepository;
    private final SimilarityService similarityService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RecommenderProperties properties;

    /**
     * Refreshes all recommendations for the given user.
     * Steps:
     * 1. Clears existing recommendations.
     * 2. Computes collaborative filtering (CF) predictions.
     * 3. Computes genre-based weights for candidate books.
     * 4. Merges CF and genre scores into hybrid scores.
     * 5. Persists top-N recommendations.
     *
     * @param userId the ID of the user to refresh recommendations for
     */
    @Transactional
    public void refreshRecommendationsForUser(Long userId) {
        // Delete old recommendations
        recommendationRepository.deleteAllByUserId(userId);

        // 1. Collaborative filtering predictions
        Map<Long, Double> cfScores = computeCollaborativePredictions(userId);
        if (cfScores.isEmpty()) {
            log.info("User {}: not enough data for collaborative filtering", userId);
            return;
        }

        // 2. Genre weights for the same candidate books
        Map<Long, Double> genreWeights = computeGenreWeights(userId, cfScores.keySet());

        // 3. Hybrid combination of CF and genre scores
        Map<Long, Double> hybridScores = mergeHybrid(cfScores, genreWeights);

        // 4. Save to database
        saveRecommendations(userId, hybridScores);
    }

    /**
     * Computes collaborative filtering predictions for all unrated books by the user.
     *
     * @param userId the target user ID
     * @return map of {bookId -> predictedScore}
     */
    private Map<Long, Double> computeCollaborativePredictions(Long userId) {
        // 1) Find top-K similar users
        Map<Long, Double> userSimilarities = similarityService.computeSimilarities(userId);
        if (userSimilarities.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2+3) Batch load all ratings for target user and neighbors
        Set<Long> neighborIds = userSimilarities.keySet();
        Set<Long> allUserIds = new HashSet<>(neighborIds);
        allUserIds.add(userId);
        List<Rating> allRatings = ratingRepository.findAllByUserIdIn(allUserIds);

        // Split into target user's ratings
        List<Rating> userRatings = allRatings.stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();

        // ...and neighbors' ratings grouped by neighbor ID
        Map<Long, Map<Long, Integer>> ratingsByNeighbor = allRatings.stream()
                .filter(r -> !r.getUserId().equals(userId))
                .collect(Collectors.groupingBy(
                        Rating::getUserId,
                        Collectors.toMap(Rating::getBookId, Rating::getScore)
                ));

        // Compute target user's mean score and set of rated book IDs
        double userMean = userRatings.stream()
                .mapToInt(Rating::getScore)
                .average().orElse(0.0);
        Set<Long> ratedBookIds = userRatings.stream()
                .map(Rating::getBookId)
                .collect(Collectors.toSet());

        // 5) Calculate predictions for each candidate book
        return calculatePredictions(userMean, ratedBookIds, ratingsByNeighbor, userSimilarities);
    }

    /**
     * Calculates predicted ratings using weighted sum of neighbors' deviations from their means.
     * Only includes books the user hasn't rated yet.
     *
     * @param userMean          the average rating of the target user
     * @param ratedBookIds      set of book IDs already rated by the user
     * @param ratingsByNeighbor map of {neighborId -> (bookId -> score)}
     * @param userSimilarities  map of {neighborId -> similarityScore}
     * @return map of {bookId -> predictedRating}
     */
    private Map<Long, Double> calculatePredictions(
            double userMean,
            Set<Long> ratedBookIds,
            Map<Long, Map<Long, Integer>> ratingsByNeighbor,
            Map<Long, Double> userSimilarities) {

        class Accumulator {
            double weightedSum = 0;
            double similaritySum = 0;
        }
        Map<Long, Accumulator> accumulators = new HashMap<>();

        // For each neighbor, accumulate contributions per book
        for (var entry : ratingsByNeighbor.entrySet()) {
            Long neighborId = entry.getKey();
            double sim = userSimilarities.getOrDefault(neighborId, 0.0);
            Map<Long, Integer> neighborRatings = entry.getValue();
            double neighborMean = neighborRatings.values().stream()
                    .mapToInt(Integer::intValue)
                    .average().orElse(0.0);

            for (var ratingEntry : neighborRatings.entrySet()) {
                Long bookId = ratingEntry.getKey();
                if (ratedBookIds.contains(bookId)) continue; // skip already rated
                double diff = ratingEntry.getValue() - neighborMean;
                Accumulator acc = accumulators.computeIfAbsent(bookId, k -> new Accumulator());
                acc.weightedSum += sim * diff;
                acc.similaritySum += Math.abs(sim);
            }
        }

        // Compute final predicted scores, filtering by minimum CF score
        Map<Long, Double> predictions = new HashMap<>();
        for (var e : accumulators.entrySet()) {
            double predicted = userMean + e.getValue().weightedSum / e.getValue().similaritySum;
            if (predicted >= properties.getMinScoreCF()) {
                predictions.put(e.getKey(), predicted);
            }
        }
        return predictions;
    }

    /**
     * Computes a genre-based weight for each candidate book, based on the user's favorite genres.
     *
     * @param userId           the user ID
     * @param candidateBookIds set of book IDs to score
     * @return map of {bookId -> genreMatchRatio (0.0 to 1.0)}
     */
    private Map<Long, Double> computeGenreWeights(Long userId, Set<Long> candidateBookIds) {
        // 0. Fetch user's favorite genres; if none, assign 0.0 to all candidates
        Set<Genre> favoriteGenres = Optional.ofNullable(
                userRepository.findFavouriteGenresById(userId)
        ).orElse(Collections.emptySet());
        if (favoriteGenres.isEmpty()) {
            return candidateBookIds.stream()
                    .collect(Collectors.toMap(id -> id, id -> 0.0));
        }
        Set<Long> favoriteGenreIds = favoriteGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        // 1. Batch load candidate books with genres
        Map<Long, Book> booksById = bookRepository.findAllWithGenresByIdIn(candidateBookIds).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        // 2. Compute match ratio: (#matching genres) / (total favorite genres)
        Map<Long, Double> genreScores = new HashMap<>();
        for (Long bookId : candidateBookIds) {
            Book book = booksById.get(bookId);
            if (book == null) continue;
            long matches = book.getGenres().stream()
                    .filter(g -> favoriteGenreIds.contains(g.getId()))
                    .count();
            double ratio = (double) matches / favoriteGenreIds.size();
            genreScores.put(bookId, ratio);
        }
        return genreScores;
    }

    /**
     * Merges collaborative filtering and genre scores into a unified hybrid score.
     *
     * @param cfScores    map of {bookId -> CF score}
     * @param genreScores map of {bookId -> genre match ratio}
     * @return map of {bookId -> hybrid score}
     */
    private Map<Long, Double> mergeHybrid(Map<Long, Double> cfScores, Map<Long, Double> genreScores) {
        Map<Long, Double> hybrid = new HashMap<>();
        for (var entry : cfScores.entrySet()) {
            Long bookId = entry.getKey();
            double cfScore = entry.getValue();
            double genreScore = genreScores.getOrDefault(bookId, 0.0) * properties.getMaxRating();
            double combined = properties.getWeightCF() * cfScore
                    + properties.getWeightGenre() * genreScore;
            if (combined >= properties.getMinScoreHybrid()) {
                hybrid.put(bookId, combined);
            }
        }
        return hybrid;
    }

    /**
     * Persists the top-N recommendations to the database, ordered by descending score.
     *
     * @param userId       the user ID
     * @param hybridScores map of {bookId -> hybrid score}
     */
    private void saveRecommendations(Long userId, Map<Long, Double> hybridScores) {
        List<Recommendation> recommendations = hybridScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(properties.getMaxPerUser())
                .map(e -> new Recommendation(userId, e.getKey(), e.getValue(), LocalDateTime.now()))
                .toList();

        recommendationRepository.saveAll(recommendations);
        log.debug("User {}: saved {} hybrid recommendations", userId, recommendations.size());
    }
}
