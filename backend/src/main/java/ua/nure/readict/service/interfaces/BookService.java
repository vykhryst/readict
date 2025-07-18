package ua.nure.readict.service.interfaces;

import org.springframework.data.domain.Page;
import ua.nure.readict.dto.book.BookRequest;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.entity.User;

import java.util.List;

public interface BookService {

    BookResponse getById(Long id);

    BookResponse create(BookRequest bookResponse);

    BookResponse update(Long id, BookRequest bookResponse);

    void deleteById(Long id);

    Page<BookResponse> getAll(String title, List<Long> genreIds, int page, int size, String sort);

    Page<BookResponse> getRecommendedBooksByUserId(
            User user,
            Long genreId,
            String sort,
            int page,
            int size
    );

}
