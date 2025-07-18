package ua.nure.readict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.readict.entity.Author;

import java.util.Map;

public interface AuthorRepository extends JpaRepository<Author, Long>, JpaSpecificationExecutor<Author> {
    Page<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

    @Query("""
                SELECT COUNT(b.id) AS bookCount,
                       COALESCE(AVG(b.averageRating), 0) AS averageRating
                FROM Book b
                WHERE b.author.id = :authorId
            """)
    Map<String, Object> getAuthorStats(@Param("authorId") Long authorId);
}