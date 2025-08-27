package ua.nure.readict.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.LibraryBookDto;
import ua.nure.readict.dto.LibrarySummaryDto;
import ua.nure.readict.entity.*;
import ua.nure.readict.repository.*;
import ua.nure.readict.service.interfaces.LibraryService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryServiceImpl implements LibraryService {

    private final ShelfRepository shelfRepo;
    private final UserBookRepository userBookRepo;
    private final BookRepository bookRepo;
    private final ReviewRepository reviewRepo;
    private final RatingRepository ratingRepo;  // ← ДОДАТИ

    public LibrarySummaryDto getLibrarySummary(Long uid) {
        return new LibrarySummaryDto(
                userBookRepo.countAll(uid),
                userBookRepo.countShelf(uid, "READ"),
                userBookRepo.countShelf(uid, "CURRENTLY_READING"),
                userBookRepo.countShelf(uid, "WANT_TO_READ")
        );
    }

    public Page<LibraryBookDto> findAllInUserLibrary(Long uid,
                                                     String shelf,
                                                     String search,
                                                     Integer rating,
                                                     LocalDate yearFrom,
                                                     LocalDate yearTo,
                                                     Set<Long> genres,
                                                     String sortCode,
                                                     Pageable pageable) {

        Specification<UserBook> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            // 1) власник
            ps.add(cb.equal(root.get("user").get("id"), uid));

            // 2) полиця
            if (shelf != null)
                ps.add(cb.equal(root.get("shelf").get("name"), shelf));

            // 3) пошук
            if (search != null && !search.isBlank()) {
                String p = "%" + search.toLowerCase() + "%";
                Join<UserBook, Book> b = root.join("book");
                Join<Book, Author> a = b.join("author");
                ps.add(cb.or(
                        cb.like(cb.lower(b.get("title")), p),
                        cb.like(cb.lower(a.get("firstName")), p),
                        cb.like(cb.lower(a.get("lastName")), p)
                ));
            }

            // 4) *** мін. середній рейтинг книги ***
            if (rating != null) {
                ps.add(cb.greaterThanOrEqualTo(
                        root.get("book").get("averageRating"),
                        rating.doubleValue()                       // Integer → Double
                ));
            }

            // 5) рік видання
            if (yearFrom != null)
                ps.add(cb.greaterThanOrEqualTo(
                        root.get("book").get("publicationDate"), yearFrom));
            if (yearTo != null)
                ps.add(cb.lessThanOrEqualTo(
                        root.get("book").get("publicationDate"), yearTo));

            // 6) жанри
            if (genres != null && !genres.isEmpty()) {
                query.distinct(true);
                Join<UserBook, Genre> g = root.join("book").join("genres");
                ps.add(g.get("id").in(genres));
            }

            return cb.and(ps.toArray(Predicate[]::new));
        };


        Sort jpaSort = switch (sortCode) {
            case "TITLE_ASC" -> Sort.by("book.title").ascending();
            case "TITLE_DESC" -> Sort.by("book.title").descending();
            case "AUTHOR_ASC" -> Sort.by("book.author.lastName").ascending();
            case "AUTHOR_DESC" -> Sort.by("book.author.lastName").descending();
            case "ADDED_AT_ASC" -> Sort.by("addedAt").ascending();
            case "ADDED_AT_DESC" -> Sort.by("addedAt").descending();
            default -> Sort.by("addedAt").descending(); // за замовчуванням
            // RATING_* обробляємо нижче
        };

        if (!sortCode.startsWith("RATING_")) {
            Page<UserBook> raw = userBookRepo.findAll(spec,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), jpaSort));
            return raw.map(ub -> toDto(uid, ub));
        }

        List<UserBook> filtered = userBookRepo.findAll(spec);
        Comparator<UserBook> cmp = Comparator.comparing(
                (UserBook ub) -> ratingRepo.findScore(uid, ub.getBook().getId()).orElse(null),
                Comparator.nullsLast(Integer::compareTo)
        );
        if (sortCode.equals("RATING_DESC")) cmp = cmp.reversed();
        filtered.sort(cmp);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<LibraryBookDto> slice = filtered.subList(start, end)
                .stream()
                .map(ub -> toDto(uid, ub))
                .toList();

        return new PageImpl<>(slice, pageable, filtered.size());
    }

    private LibraryBookDto toDto(Long uid, UserBook ub) {
        Book b = ub.getBook();

        Review review = reviewRepo.findById(new ReviewId(uid, b.getId()))
                .orElse(null);

        String reviewContent = review != null ? review.getContent() : null;

        String author = b.getAuthor().getFirstName() + " " + b.getAuthor().getLastName();
        Long authorId = b.getAuthor().getId();
        Double myRate = ratingRepo.findScore(uid, b.getId()).map(Integer::doubleValue).orElse(null);

        return new LibraryBookDto(
                b.getId(),
                b.getTitle(),
                b.getCover(),
                author,
                authorId,
                myRate,
                ub.getAddedAt(),
                ub.getShelf().getName(),
                reviewContent,
                b.getPublicationDate(),
                b.getGenres().stream().map(Genre::getId).collect(Collectors.toSet())
        );
    }

    public Optional<String> findShelf(Long userId, Long bookId) {
        return userBookRepo.findByUserIdAndBookId(userId, bookId)
                .map(ub -> ub.getShelf().getName());
    }

    public void moveBookToShelf(User user, Long bookId, String shelfName) {
        Shelf shelf = shelfRepo.findByName(shelfName.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Shelf not found"));
        Book book = bookRepo.getReferenceById(bookId);

        UserBookId pk = new UserBookId();
        pk.setUserId(user.getId());
        pk.setBookId(book.getId());

        UserBook link = userBookRepo.findById(pk).orElseGet(() -> {
            UserBook ub = new UserBook();
            ub.setId(pk);
            ub.setUser(user);
            ub.setBook(book);
            return ub;
        });

        link.setShelf(shelf);
        userBookRepo.save(link);
    }

    public void removeFromLibrary(Long userId, Long bookId) {
        userBookRepo.deleteByUserIdAndBookId(userId, bookId);
    }
}
