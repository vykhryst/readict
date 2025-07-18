package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.readict.entity.BookGenre;
import ua.nure.readict.entity.BookGenreId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookGenreRepository extends JpaRepository<BookGenre, BookGenreId> {

    /** Повертає {bookId, cnt} — кількість жанрів книги, що збігаються з favIds. */
    @Query("""
            SELECT bg.id.bookId, COUNT(bg.id.genreId)
              FROM BookGenre bg
             WHERE bg.id.bookId IN :bookIds
               AND bg.id.genreId IN :favIds
             GROUP BY bg.id.bookId
            """)
    List<Object[]> countMatches(@Param("bookIds") Set<Long> bookIds,
                                @Param("favIds")  Set<Long> favIds);
}