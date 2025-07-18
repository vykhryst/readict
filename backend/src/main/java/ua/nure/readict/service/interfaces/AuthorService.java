package ua.nure.readict.service.interfaces;

import org.springframework.data.domain.Page;
import ua.nure.readict.dto.AuthorDto;
import ua.nure.readict.dto.book.BookResponse;

import java.util.Map;

public interface AuthorService {
    Page<AuthorDto> getAll(String name, int page, int size, String sort);
    AuthorDto getById(Long id);
    AuthorDto create(AuthorDto authorDto);
    AuthorDto update(Long id, AuthorDto authorDto);
    void deleteById(Long id);
    Map<String, Object> getAuthorStats(Long id);

    Page<BookResponse> getBooksByAuthor(Long id, int page, int size, String sort);
}
