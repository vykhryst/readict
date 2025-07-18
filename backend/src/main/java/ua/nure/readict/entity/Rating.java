package ua.nure.readict.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating")
@IdClass(RatingId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Getter
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "score", nullable = false)
    private Integer score;


    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "added_at")
    private LocalDateTime addedAt = LocalDateTime.now();

    // Зручний конструктор
    public Rating(Long userId, Long bookId, Integer score) {
        this.userId = userId;
        this.bookId = bookId;
        this.score = score;
    }

}