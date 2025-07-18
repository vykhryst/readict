package ua.nure.readict.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ua.nure.readict.dto.LibraryBookDto;
import ua.nure.readict.dto.LibrarySummaryDto;
import ua.nure.readict.entity.*;
import ua.nure.readict.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryServiceImplTest {

    @Mock
    private ShelfRepository shelfRepo;

    @Mock
    private UserBookRepository userBookRepo;

    @Mock
    private BookRepository bookRepo;

    @Mock
    private ReviewRepository reviewRepo;

    @Mock
    private RatingRepository ratingRepo;

    @InjectMocks
    private LibraryServiceImpl libraryServiceImpl;

    @Captor
    private ArgumentCaptor<UserBook> userBookCaptor;

    @Nested
    @DisplayName("Tests for retrieving library getLibrarySummary")
    class LibrarySummaryTests {

        @Test
        @DisplayName("Should return correct library getLibrarySummary")
        void shouldReturnCorrectLibrarySummary() {
            // Arrange
            Long userId = 1L;

            when(userBookRepo.countAll(userId)).thenReturn(10L);
            when(userBookRepo.countShelf(userId, "READ")).thenReturn(5L);
            when(userBookRepo.countShelf(userId, "CURRENTLY_READING")).thenReturn(2L);
            when(userBookRepo.countShelf(userId, "WANT_TO_READ")).thenReturn(3L);

            // Act
            LibrarySummaryDto result = libraryServiceImpl.getLibrarySummary(userId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.total()).isEqualTo(10L);
            assertThat(result.read()).isEqualTo(5L);
            assertThat(result.reading()).isEqualTo(2L);
            assertThat(result.want()).isEqualTo(3L);

            verify(userBookRepo).countAll(userId);
            verify(userBookRepo).countShelf(userId, "READ");
            verify(userBookRepo).countShelf(userId, "CURRENTLY_READING");
            verify(userBookRepo).countShelf(userId, "WANT_TO_READ");
        }
    }

    @Nested
    @DisplayName("Tests for adding and moving books in library")
    class AddingAndMovingBooksTests {

        @Test
        @DisplayName("Should add a book to library on a specified shelf")
        void shouldAddBookToLibraryOnSpecifiedShelf() {
            // Arrange
            Long userId = 1L;
            Long bookId = 2L;
            String shelfName = "WANT_TO_READ";

            User user = new User();
            user.setId(userId);

            Book book = new Book();
            book.setId(bookId);

            Shelf shelf = new Shelf();
            shelf.setId(1L);
            shelf.setName(shelfName);

            when(shelfRepo.findByName(shelfName)).thenReturn(Optional.of(shelf));
            when(bookRepo.getReferenceById(bookId)).thenReturn(book);
            when(userBookRepo.findById(any(UserBookId.class))).thenReturn(Optional.empty());

            // Act
            libraryServiceImpl.moveBookToShelf(user, bookId, shelfName);

            // Assert
            verify(userBookRepo).save(userBookCaptor.capture());

            UserBook capturedUserBook = userBookCaptor.getValue();
            assertThat(capturedUserBook.getId().getUserId()).isEqualTo(userId);
            assertThat(capturedUserBook.getId().getBookId()).isEqualTo(bookId);
            assertThat(capturedUserBook.getUser()).isEqualTo(user);
            assertThat(capturedUserBook.getBook()).isEqualTo(book);
            assertThat(capturedUserBook.getShelf()).isEqualTo(shelf);
            assertThat(capturedUserBook.getAddedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should move a book from one shelf to another")
        void shouldMoveBookFromOneShelfToAnother() {
            // Arrange
            Long userId = 1L;
            Long bookId = 2L;
            String oldShelfName = "WANT_TO_READ";
            String newShelfName = "CURRENTLY_READING";

            User user = new User();
            user.setId(userId);

            Book book = new Book();
            book.setId(bookId);

            Shelf oldShelf = new Shelf();
            oldShelf.setId(1L);
            oldShelf.setName(oldShelfName);

            Shelf newShelf = new Shelf();
            newShelf.setId(2L);
            newShelf.setName(newShelfName);

            UserBookId id = new UserBookId();
            id.setUserId(userId);
            id.setBookId(bookId);

            UserBook existingUserBook = new UserBook();
            existingUserBook.setId(id);
            existingUserBook.setUser(user);
            existingUserBook.setBook(book);
            existingUserBook.setShelf(oldShelf);
            existingUserBook.setAddedAt(LocalDateTime.now().minusDays(1));

            when(shelfRepo.findByName(newShelfName)).thenReturn(Optional.of(newShelf));
            when(bookRepo.getReferenceById(bookId)).thenReturn(book);
            when(userBookRepo.findById(any(UserBookId.class))).thenReturn(Optional.of(existingUserBook));

            // Act
            libraryServiceImpl.moveBookToShelf(user, bookId, newShelfName);

            // Assert
            verify(userBookRepo).save(userBookCaptor.capture());

            UserBook capturedUserBook = userBookCaptor.getValue();
            assertThat(capturedUserBook.getId()).isEqualTo(id);
            assertThat(capturedUserBook.getShelf()).isEqualTo(newShelf);
            // Verify that added date is preserved
            assertThat(capturedUserBook.getAddedAt()).isEqualTo(existingUserBook.getAddedAt());
        }

        @Test
        @DisplayName("Should find shelf for a book in user's library")
        void shouldFindShelfForBookInUsersLibrary() {
            // Arrange
            Long userId = 1L;
            Long bookId = 2L;
            String shelfName = "READ";

            UserBook userBook = new UserBook();
            Shelf shelf = new Shelf();
            shelf.setName(shelfName);
            userBook.setShelf(shelf);

            when(userBookRepo.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(userBook));

            // Act
            Optional<String> result = libraryServiceImpl.findShelf(userId, bookId);

            // Assert
            verify(userBookRepo).findByUserIdAndBookId(userId, bookId);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(shelfName);
        }

        @Test
        @DisplayName("Should return empty when book is not in user's library")
        void shouldReturnEmptyWhenBookIsNotInUsersLibrary() {
            // Arrange
            Long userId = 1L;
            Long bookId = 2L;

            when(userBookRepo.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());

            // Act
            Optional<String> result = libraryServiceImpl.findShelf(userId, bookId);

            // Assert
            verify(userBookRepo).findByUserIdAndBookId(userId, bookId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests for removing books from library")
    class RemoveBooksTests {

        @Test
        @DisplayName("Should removeFromLibrary a book from user's library")
        void shouldRemoveBookFromUsersLibrary() {
            // Arrange
            Long userId = 1L;
            Long bookId = 2L;

            // Act
            libraryServiceImpl.removeFromLibrary(userId, bookId);

            // Assert
            verify(userBookRepo).deleteByUserIdAndBookId(userId, bookId);
        }
    }

    @Nested
    @DisplayName("Tests for library book listing and filtering")
    class LibraryBookListingTests {

        private UserBook createUserBook(Long userId, Long bookId, String shelfName) {
            UserBookId id = new UserBookId();
            id.setUserId(userId);
            id.setBookId(bookId);

            User user = new User();
            user.setId(userId);

            Book book = new Book();
            book.setId(bookId);
            book.setTitle("Test Book " + bookId);
            book.setAverageRating(4.5);
            book.setPublicationDate(LocalDate.of(2020, 1, 1));

            Author author = new Author();
            author.setId(1L);
            author.setFirstName("John");
            author.setLastName("Doe");
            book.setAuthor(author);

            Set<Genre> genres = new HashSet<>();
            Genre genre = new Genre();
            genre.setId(1L);
            genre.setName("Fiction");
            genres.add(genre);
            book.setGenres(genres);

            Shelf shelf = new Shelf();
            shelf.setId(1L);
            shelf.setName(shelfName);

            UserBook userBook = new UserBook();
            userBook.setId(id);
            userBook.setUser(user);
            userBook.setBook(book);
            userBook.setShelf(shelf);
            userBook.setAddedAt(LocalDateTime.now());

            return userBook;
        }

        @Test
        @DisplayName("Should return findAllInUserLibrary of books in user's library")
        void shouldReturnPageOfBooksInUsersLibrary() {
            // Arrange
            Long userId = 1L;
            int page = 0;
            int size = 10;
            String sortCode = "ADDED_AT_DESC";

            List<UserBook> userBooks = new ArrayList<>();
            userBooks.add(createUserBook(userId, 1L, "READ"));
            userBooks.add(createUserBook(userId, 2L, "CURRENTLY_READING"));
            userBooks.add(createUserBook(userId, 3L, "WANT_TO_READ"));

            Page<UserBook> userBookPage = new PageImpl<>(userBooks, PageRequest.of(page, size), 3);

            when(userBookRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userBookPage);

            // Mock rating and review for each book
            for (UserBook ub : userBooks) {
                lenient().when(ratingRepo.findScore(userId, ub.getBook().getId())).thenReturn(Optional.of(4));
                lenient().when(reviewRepo.findById(any(ReviewId.class))).thenReturn(Optional.empty());
            }

            // Act
            Page<LibraryBookDto> result = libraryServiceImpl.findAllInUserLibrary(
                    userId, null, null, null, null, null, null, sortCode, PageRequest.of(page, size));

            // Assert
            verify(userBookRepo).findAll(any(Specification.class), any(Pageable.class));

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent().get(0).id()).isNotNull();
            assertThat(result.getContent().get(0).title()).isNotNull();
            assertThat(result.getContent().get(0).author()).isEqualTo("John Doe");
            assertThat(result.getContent().get(0).myRating()).isEqualTo(4.0);
        }

        @Test
        @DisplayName("Should filter books by shelf")
        void shouldFilterBooksByShelf() {
            // Arrange
            Long userId = 1L;
            String shelf = "READ";
            int page = 0;
            int size = 10;
            String sortCode = "ADDED_AT_DESC";

            List<UserBook> userBooks = new ArrayList<>();
            userBooks.add(createUserBook(userId, 1L, shelf));
            userBooks.add(createUserBook(userId, 2L, shelf));

            Page<UserBook> userBookPage = new PageImpl<>(userBooks, PageRequest.of(page, size), 2);

            when(userBookRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userBookPage);

            // Mock rating and review for each book
            for (UserBook ub : userBooks) {
                lenient().when(ratingRepo.findScore(userId, ub.getBook().getId())).thenReturn(Optional.of(4));
                lenient().when(reviewRepo.findById(any(ReviewId.class))).thenReturn(Optional.empty());
            }

            // Act
            Page<LibraryBookDto> result = libraryServiceImpl.findAllInUserLibrary(
                    userId, shelf, null, null, null, null, null, sortCode, PageRequest.of(page, size));

            // Assert
            verify(userBookRepo).findAll(any(Specification.class), any(Pageable.class));

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).shelf()).isEqualTo(shelf);
            assertThat(result.getContent().get(1).shelf()).isEqualTo(shelf);
        }

        @Test
        @DisplayName("Should filter books by minimum rating")
        void shouldFilterBooksByMinimumRating() {
            // Arrange
            Long userId = 1L;
            Integer minRating = 4;
            int page = 0;
            int size = 10;
            String sortCode = "ADDED_AT_DESC";

            List<UserBook> userBooks = new ArrayList<>();
            userBooks.add(createUserBook(userId, 1L, "READ"));
            userBooks.add(createUserBook(userId, 2L, "READ"));

            Page<UserBook> userBookPage = new PageImpl<>(userBooks, PageRequest.of(page, size), 2);

            when(userBookRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userBookPage);

            // Mock rating and review for each book
            for (UserBook ub : userBooks) {
                lenient(). when(ratingRepo.findScore(userId, ub.getBook().getId())).thenReturn(Optional.of(4));
                lenient().when(reviewRepo.findById(any(ReviewId.class))).thenReturn(Optional.empty());
            }

            // Act
            Page<LibraryBookDto> result = libraryServiceImpl.findAllInUserLibrary(
                    userId, null, null, minRating, null, null, null, sortCode, PageRequest.of(page, size));

            // Assert
            verify(userBookRepo).findAll(any(Specification.class), any(Pageable.class));

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("Should sort books by user's rating")
        void shouldSortBooksByUsersRating() {
            // Arrange
            Long userId = 1L;
            int page = 0;
            int size = 10;
            String sortCode = "RATING_DESC";

            UserBook book1 = createUserBook(userId, 1L, "READ");
            UserBook book2 = createUserBook(userId, 2L, "READ");
            UserBook book3 = createUserBook(userId, 3L, "READ");

            List<UserBook> userBooks = new ArrayList<>();
            userBooks.add(book1);
            userBooks.add(book2);
            userBooks.add(book3);

            when(userBookRepo.findAll(any(Specification.class))).thenReturn(userBooks);

            // Different ratings for sorting
            when(ratingRepo.findScore(userId, 1L)).thenReturn(Optional.of(5));
            when(ratingRepo.findScore(userId, 2L)).thenReturn(Optional.of(3));
            when(ratingRepo.findScore(userId, 3L)).thenReturn(Optional.of(4));

            when(reviewRepo.findById(any(ReviewId.class))).thenReturn(Optional.empty());

            // Act
            Page<LibraryBookDto> result = libraryServiceImpl.findAllInUserLibrary(
                    userId, null, null, null, null, null, null, sortCode, PageRequest.of(page, size));

            // Assert
            verify(userBookRepo).findAll(any(Specification.class));

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            // Books should be sorted by rating in descending order: 5, 4, 3
            assertThat(result.getContent().get(0).myRating()).isEqualTo(5.0);
            assertThat(result.getContent().get(1).myRating()).isEqualTo(4.0);
            assertThat(result.getContent().get(2).myRating()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("Should include review content in book dto")
        void shouldIncludeReviewContentInBookDto() {
            // Arrange
            Long userId = 1L;
            Long bookId = 1L;
            int page = 0;
            int size = 10;
            String sortCode = "ADDED_AT_DESC";
            String reviewContent = "This is a great book!";

            UserBook userBook = createUserBook(userId, bookId, "READ");
            List<UserBook> userBooks = List.of(userBook);
            Page<UserBook> userBookPage = new PageImpl<>(userBooks, PageRequest.of(page, size), 1);

            when(userBookRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userBookPage);
            when(ratingRepo.findScore(userId, bookId)).thenReturn(Optional.of(5));

            // Mock review
            Review review = new Review();
            ReviewId reviewId = new ReviewId();
            reviewId.setUserId(userId);
            reviewId.setBookId(bookId);
            review.setId(reviewId);
            review.setContent(reviewContent);

            when(reviewRepo.findById(any(ReviewId.class))).thenReturn(Optional.of(review));

            // Act
            Page<LibraryBookDto> result = libraryServiceImpl.findAllInUserLibrary(
                    userId, null, null, null, null, null, null, sortCode, PageRequest.of(page, size));

            // Assert
            verify(userBookRepo).findAll(any(Specification.class), any(Pageable.class));
            verify(reviewRepo).findById(any(ReviewId.class));

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).review()).isEqualTo(reviewContent);
        }
    }
}