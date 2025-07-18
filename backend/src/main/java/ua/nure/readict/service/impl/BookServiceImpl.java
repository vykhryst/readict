package ua.nure.readict.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.book.BookRequest;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.entity.*;
import ua.nure.readict.exception.FieldNotUniqueException;
import ua.nure.readict.mapper.BookMapper;
import ua.nure.readict.repository.*;
import ua.nure.readict.service.interfaces.BookService;
import ua.nure.readict.util.Constants;
import ua.nure.readict.util.SortingUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookServiceImpl extends AbstractService implements BookService {

    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;
    private final RecommendationRepository recommendationRepository;
    private final SeriesRepository seriesRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final TropeRepository tropeRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<BookResponse> getAll(String title, List<Long> genreIds, int page, int size, String sort) {
        Sort defaultSort = SortingUtil.getSort(
                sort,
                "averageRating",
                Sort.Direction.DESC,
                Book.class
        );

        Pageable pageable = PageRequest.of(page, size, defaultSort);

        Page<Book> booksPage = bookRepository.findAll((root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            return buildBookPredicates(root, cb, title, genreIds);
        }, pageable);


        return booksPage.map(bookMapper::toResponse);
    }


    @Override
    public Page<BookResponse> getRecommendedBooksByUserId(User user, Long genreId, String sort, int page, int size) {
        Long userId = user.getId();

        /* -------- чи задано користувачем власне сортування? -------- */
        boolean customSorting = sort != null && !sort.isBlank();

        Pageable pageable;
        Page<Book> result;

        if (customSorting) {
            Sort userSort = SortingUtil.getSort(
                    sort,
                    "title",
                    Sort.Direction.ASC,
                    Book.class);

            pageable = PageRequest.of(page, size, userSort);

            result = (genreId == null)
                    ? recommendationRepository.findByUser(userId, pageable)
                    : recommendationRepository.findByUserAndGenre(userId, genreId, pageable);

        } else {
            pageable = PageRequest.of(page, size);   // Sort не потрібен

            result = (genreId == null)
                    ? recommendationRepository.findTopByUser(userId, pageable)
                    : recommendationRepository.findTopByUserAndGenre(userId, genreId, pageable);
        }

        if (result.hasContent()) {
            return result.map(bookMapper::toResponse);
        }
        return getFallbackBooks(user, page, size);
    }


    private Page<BookResponse> getFallbackBooks(User user, int page, int size) {
        // Дістаємо улюблені жанри користувача
        Set<Long> favGenreIds = user.getFavouriteGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        if (favGenreIds.isEmpty()) {
            return getGlobalTopBooks(page, size);
        }

        // Які книжки вже оцінив користувач
        Set<Long> ratedBookIds = ratingRepository.findAllByUserId(user.getId()).stream()
                .map(Rating::getBookId).collect(Collectors.toSet());

        // Побудова fallback‑Pageable: сортування за avgRating DESC, ratingCount DESC
        Sort fallbackSort = Sort.by(Sort.Order.desc("averageRating"),
                Sort.Order.desc("ratingCount"));
        Pageable fallbackPageable = PageRequest.of(page, size, fallbackSort);

        // Витягуємо fallback‑книги
        Page<Book> fallbackPage = bookRepository.findByGenresInExcludeBooks(
                favGenreIds,
                ratedBookIds,
                fallbackPageable
        );
        return fallbackPage.map(bookMapper::toResponse);
    }


    private Page<BookResponse> getGlobalTopBooks(int page, int size) {
        Page<Book> bookPage = bookRepository
                .findAllByAverageRatingGreaterThanOrderByRatingCount(4.4, PageRequest.of(page, size));
        return bookPage.map(bookMapper::toResponse);
    }


    @Override
    public BookResponse getById(Long id) {
        Book book = findEntityByIdOrThrow(id, bookRepository, Constants.BOOK_NOT_FOUND);
        return bookMapper.toResponse(book);
    }

    @Override
    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new FieldNotUniqueException(String.format("Book with ISBN '%s' already exists.", request.isbn()));
        }
        Author author = findEntityByIdOrThrow(request.authorId(), authorRepository, Constants.AUTHOR_NOT_FOUND);
        Series series = findEntityByIdOrNull(request.seriesId(), seriesRepository, Constants.SERIES_NOT_FOUND);
        Set<Genre> genres = findEntitiesByIdsOrThrow(request.genreIds(), genreRepository, Constants.GENRE_NOT_FOUND);
        Set<Trope> tropes = findEntitiesByIdsOrThrow(request.tropeIds(), tropeRepository, Constants.TROPE_NOT_FOUND);

        Book newBook = bookMapper.toEntity(request);
        newBook.setAuthor(author);
        newBook.setSeries(series);
        newBook.setGenres(genres);
        newBook.setTropes(tropes);

        return bookMapper.toResponse(bookRepository.save(newBook));
    }

    @Override
    public BookResponse update(Long id, BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new FieldNotUniqueException(String.format("Book with ISBN '%s' already exists.", request.isbn()));
        }
        Book existingBook = findEntityByIdOrThrow(id, bookRepository, Constants.BOOK_NOT_FOUND);

        if (request.authorId() != null) {
            existingBook.setAuthor(findEntityByIdOrThrow(request.authorId(), authorRepository, Constants.AUTHOR_NOT_FOUND));
        }

        existingBook.setSeries(findEntityByIdOrNull(request.seriesId(), seriesRepository, Constants.SERIES_NOT_FOUND));
        updateGenresIfChanged(request.genreIds(), existingBook);
        updateTropesIfChanged(request.tropeIds(), existingBook);

        bookMapper.partialUpdate(request, existingBook);
        return bookMapper.toResponse(bookRepository.save(existingBook));
    }

    @Override
    public void deleteById(Long id) {
        checkEntityExistsOrThrow(id, bookRepository, Constants.BOOK_NOT_FOUND);
        bookRepository.deleteById(id);
    }

    private Predicate buildBookPredicates(Root<Book> root,
                                          CriteriaBuilder cb,
                                          String title,
                                          List<Long> genreIds) {
        List<Predicate> predicates = new ArrayList<>();

        // пошук за назвою (LIKE)
        if (title != null && !title.isBlank()) {
            predicates.add(
                    cb.like(cb.lower(root.get("title")),
                            "%" + title.toLowerCase() + "%"));
        }

        // фільтр за кількома жанрами (IN)
        if (genreIds != null && !genreIds.isEmpty()) {
            predicates.add(
                    root.join("genres")
                            .get("id")
                            .in(genreIds));
        }

        cb.createQuery().distinct(true);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void updateGenresIfChanged(Set<Long> genreIds, Book book) {
        if (genreIds == null || genreIds.isEmpty()) {
            book.setGenres(Collections.emptySet());
            return;
        }

        Set<Long> existingGenreIds = book.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        if (!existingGenreIds.equals(genreIds)) {
            book.setGenres(findEntitiesByIdsOrThrow(genreIds, genreRepository, Constants.GENRE_NOT_FOUND));
        }
    }

    private void updateTropesIfChanged(Set<Long> tropeIds, Book book) {
        if (tropeIds == null || tropeIds.isEmpty()) {
            book.setTropes(Collections.emptySet());
            return;
        }

        Set<Long> existingTropeIds = book.getTropes().stream()
                .map(Trope::getId)
                .collect(Collectors.toSet());

        if (!existingTropeIds.equals(tropeIds)) {
            book.setTropes(findEntitiesByIdsOrThrow(tropeIds, tropeRepository, Constants.TROPE_NOT_FOUND));
        }
    }
}
