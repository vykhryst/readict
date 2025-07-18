package ua.nure.readict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.readict.entity.Book;
import ua.nure.readict.entity.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn);

    @EntityGraph(attributePaths = {"author", "series"})
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g IN :genres")
    List<Book> findDistinctByGenresIn(@Param("genres") Collection<Genre> genres, Pageable pageable);

    Page<Book> findAllByAverageRatingGreaterThanOrderByRatingCount(Double averageRatingIsGreaterThan, Pageable pageable);


    List<Book> findAllByIdIn(Collection<Long> ids);

    @Query("select distinct b from Book b join fetch b.genres g where b.id in :ids")
    List<Book> findAllWithGenresByIdIn(Set<Long> ids);

    @Query("""
            SELECT DISTINCT b
              FROM Book b
              JOIN b.genres g
             WHERE g.id IN :genreIds
               AND b.id NOT IN :excludeIds
            """)
    Page<Book> findByGenresInExcludeBooks(
            @Param("genreIds") Collection<Long> genreIds,
            @Param("excludeIds") Collection<Long> excludeIds,
            Pageable pageable
    );

    List<Book> findAllByIdBetween(Long idAfter, Long idBefore);


    Page<Book> findAllByAuthorId(Long authorId, Pageable pageable);


    /**
     * Підрахунок кількості книг у серії
     */
    @Query("SELECT COUNT(b) FROM Book b WHERE b.series.id = :seriesId")
    Long countBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * Отримання середнього рейтингу книг у серії
     */
    @Query("SELECT AVG(b.averageRating) FROM Book b WHERE b.series.id = :seriesId AND b.averageRating IS NOT NULL")
    Double getAverageRatingBySeriesId(@Param("seriesId") Long seriesId);

    /**
     * Знайти книги за серією з сортуванням за номером у серії
     */
    @Query("SELECT b FROM Book b WHERE b.series.id = :seriesId ORDER BY b.seriesNumber ASC, b.averageRating DESC")
    Page<Book> findBySeriesIdOrderBySeriesNumber(@Param("seriesId") Long seriesId, Pageable pageable);

    /**
     * Знайти книги автора з сортуванням за серією та номером
     */
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId ORDER BY b.series.name ASC, b.seriesNumber ASC, b.averageRating DESC")
    Page<Book> findByAuthorIdOrderBySeriesAndNumber(@Param("authorId") Long authorId, Pageable pageable);
}

