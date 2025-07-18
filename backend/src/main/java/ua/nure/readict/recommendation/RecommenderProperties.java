package ua.nure.readict.recommendation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class RecommenderProperties {

    /**
     * Minimum number of common ratings required to consider two users comparable.
     */
    private int minCommon = 2;

    /**
     * Number of nearest neighbors to include when computing cosine similarity.
     */
    private int kNeighbors = 100;

    /**
     * Minimum raw collaborative-filtering score below which we do not persist a recommendation.
     */
    private double minScoreCF = 2.0;

    /**
     * Final threshold for the hybrid score below which recommendations are discarded.
     */
    private double minScoreHybrid = 2.5;

    /**
     * Maximum number of books to store in the recommendations table per user.
     */
    private int maxPerUser = 50;

    /**
     * Weight of the collaborative-filtering component in the hybrid score.
     */
    private double weightCF = 0.7;

    /**
     * Weight of the content-based (genre) component in the hybrid score.
     */
    private double weightGenre = 0.3;

    /**
     * Maximum possible user rating value (used for normalization).
     */
    private int maxRating = 5;
}
