package ua.nure.readict.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "recommendation")
@IdClass(RecommendationId.class)
public class Recommendation {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "predicted_score", nullable = false)
    private Double predictedScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Recommendation(Long userId, Long bookId, double score, LocalDateTime ts) {
        this.userId = userId;
        this.bookId = bookId;
        this.predictedScore = score;
        this.createdAt = ts;
    }
}

