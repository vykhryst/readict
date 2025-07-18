package ua.nure.readict.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.AuthorDto;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.entity.Author;
import ua.nure.readict.entity.Book;
import ua.nure.readict.mapper.AuthorMapper;
import ua.nure.readict.mapper.BookMapper;
import ua.nure.readict.repository.AuthorRepository;
import ua.nure.readict.repository.BookRepository;
import ua.nure.readict.service.interfaces.AuthorService;
import ua.nure.readict.util.Constants;
import ua.nure.readict.util.SortingUtil;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl extends AbstractService implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    @Override
    public Page<AuthorDto> getAll(String name, int page, int size, String sort) {
        Sort defaultSort = SortingUtil.getSort(
                sort, "lastName", Sort.Direction.ASC, Author.class
        );
        Pageable pageable = PageRequest.of(page, size, defaultSort);

        Page<Author> authors = name != null && !name.isBlank()
                ? authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable)
                : authorRepository.findAll(pageable);

        return authors.map(authorMapper::toDto);
    }

    @Override
    public AuthorDto getById(Long id) {
        return authorMapper.toDto(findEntityByIdOrThrow(id, authorRepository, Constants.AUTHOR_NOT_FOUND));
    }

    @Override
    public AuthorDto create(AuthorDto authorDto) {
        Author author = authorMapper.toEntity(authorDto);
        return authorMapper.toDto(authorRepository.save(author));
    }

    @Override
    public AuthorDto update(Long id, AuthorDto authorDto) {
        Author existingAuthor = findEntityByIdOrThrow(id, authorRepository, Constants.AUTHOR_NOT_FOUND);
        authorMapper.partialUpdate(authorDto, existingAuthor);
        return authorMapper.toDto(authorRepository.save(existingAuthor));
    }

    @Override
    public void deleteById(Long id) {
        checkEntityExistsOrThrow(id, authorRepository, Constants.AUTHOR_NOT_FOUND);
        authorRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> getAuthorStats(Long id) {
        findEntityByIdOrThrow(id, authorRepository, Constants.AUTHOR_NOT_FOUND);
        return authorRepository.getAuthorStats(id);
    }

    @Override
    public Page<BookResponse> getBooksByAuthor(Long authorId, int page, int size, String sort) {
        Sort sortObj = SortingUtil.getSort(
                sort, "averageRating", Sort.Direction.DESC, Book.class
        );
        PageRequest pageRequest = PageRequest.of(page, size, sortObj);
        Page<Book> books = bookRepository.findAllByAuthorId(authorId, pageRequest);
        return books.map(bookMapper::toResponse);
    }


}
