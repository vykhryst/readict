package ua.nure.readict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.readict.entity.Book;
import ua.nure.readict.entity.Recommendation;
import ua.nure.readict.entity.RecommendationId;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, RecommendationId> {
    @Modifying
    @Query("DELETE FROM Recommendation r WHERE r.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    List<Recommendation> findAllByUserId(Long userId);


    /* ---------- DEFAULT: із ORDER BY predictedScore DESC ---------- */

    @Query(value = """
            SELECT b
              FROM Book           b
              JOIN Recommendation r ON r.bookId = b.id
             WHERE r.userId = :userId
             ORDER BY r.predictedScore DESC
            """,
            countQuery = """
            SELECT COUNT(r)
              FROM Recommendation r
             WHERE r.userId = :userId
            """)
    Page<Book> findTopByUser(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
            SELECT b
              FROM Book           b
              JOIN Recommendation r ON r.bookId = b.id
              JOIN b.genres       g
             WHERE r.userId = :userId
               AND g.id   = :genreId
             ORDER BY r.predictedScore DESC
            """,
            countQuery = """
            SELECT COUNT(r)
              FROM Recommendation r
              JOIN Book  b ON b.id = r.bookId
              JOIN b.genres g
             WHERE r.userId = :userId
               AND g.id   = :genreId
            """)
    Page<Book> findTopByUserAndGenre(@Param("userId") Long userId,
                                     @Param("genreId") Long genreId,
                                     Pageable pageable);

    /* ---------- CUSTOM: БЕЗ ORDER BY (додаємо тільки user-сортування) ---------- */

    @Query(value = """
            SELECT b
              FROM Book           b
              JOIN Recommendation r ON r.bookId = b.id
             WHERE r.userId = :userId
            """,
            countQuery = """
            SELECT COUNT(r)
              FROM Recommendation r
             WHERE r.userId = :userId
            """)
    Page<Book> findByUser(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
            SELECT b
              FROM Book           b
              JOIN Recommendation r ON r.bookId = b.id
              JOIN b.genres       g
             WHERE r.userId = :userId
               AND g.id   = :genreId
            """,
            countQuery = """
            SELECT COUNT(r)
              FROM Recommendation r
              JOIN Book  b ON b.id = r.bookId
              JOIN b.genres g
             WHERE r.userId = :userId
               AND g.id   = :genreId
            """)
    Page<Book> findByUserAndGenre(@Param("userId") Long userId,
                                  @Param("genreId") Long genreId,
                                  Pageable pageable);

}