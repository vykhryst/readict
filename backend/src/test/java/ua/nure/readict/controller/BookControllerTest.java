package ua.nure.readict.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.nure.readict.dto.AuthorDto;
import ua.nure.readict.dto.GenreDto;
import ua.nure.readict.dto.SeriesDto;
import ua.nure.readict.dto.TropeDto;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.service.interfaces.BookService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private BookResponse createTestBookResponse(Long id) {
        return new BookResponse(
                id,
                "Test Book " + id,
                new SeriesDto(1L, "Test Series"),
                1,
                new AuthorDto(1L, "John", null, "Doe", "Author biography"),
                "Test annotation for book " + id,
                300,
                LocalDate.of(2023, 1, 1),
                "978-3-16-148410-" + id,
                "English",
                4.5,
                "Test Publisher",
                1,
                100,
                200,
                LocalDateTime.now(),
                Set.of(new GenreDto(1L, "Fiction")),
                Set.of(new TropeDto(1L, "Hero's Journey")),
                "https://example.com/cover" + id + ".jpg"
        );
    }

    private List<BookResponse> createTestBookResponses(int count) {
        List<BookResponse> books = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            books.add(createTestBookResponse((long) i));
        }
        return books;
    }

    @Test
    @DisplayName("Should return all books with default parameters")
    void shouldReturnAllBooksWithDefaultParameters() {
        // Arrange
        String title = null;
        List<Long> genreIds = null;
        int page = 0;
        int size = 10;
        String sort = "title,asc";

        List<BookResponse> bookResponses = createTestBookResponses(10);
        Page<BookResponse> bookPage = new PageImpl<>(bookResponses);

        when(bookService.getAll(title, genreIds, page, size, sort)).thenReturn(bookPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(title, genreIds, page, size, sort);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(10);
        verify(bookService).getAll(title, genreIds, page, size, sort);
    }

    @Test
    @DisplayName("Should search books by title")
    void shouldFilterBooksByTitle() {
        // Arrange
        String title = "Harry Potter";
        List<Long> genreIds = null;
        int page = 0;
        int size = 10;
        String sort = "title,asc";

        List<BookResponse> bookResponses = createTestBookResponses(3);
        Page<BookResponse> bookPage = new PageImpl<>(bookResponses);

        when(bookService.getAll(title, genreIds, page, size, sort)).thenReturn(bookPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(title, genreIds, page, size, sort);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(3);
        verify(bookService).getAll(title, genreIds, page, size, sort);
    }

    @Test
    @DisplayName("Should filter books by genres")
    void shouldFilterBooksByGenres() {
        // Arrange
        String title = null;
        List<Long> genreIds = List.of(1L, 2L);
        int page = 0;
        int size = 10;
        String sort = "title,asc";

        List<BookResponse> bookResponses = createTestBookResponses(5);
        Page<BookResponse> bookPage = new PageImpl<>(bookResponses);

        when(bookService.getAll(title, genreIds, page, size, sort)).thenReturn(bookPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(title, genreIds, page, size, sort);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(5);
        verify(bookService).getAll(title, genreIds, page, size, sort);
    }

    @Test
    @DisplayName("Should return book details by ID")
    void shouldReturnBookDetailsById() {
        // Arrange
        Long bookId = 1L;
        BookResponse bookResponse = createTestBookResponse(bookId);

        when(bookService.getById(bookId)).thenReturn(bookResponse);

        // Act
        ResponseEntity<BookResponse> response = bookController.getBookById(bookId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(bookId);
        assertThat(response.getBody().title()).isEqualTo("Test Book " + bookId);
        assertThat(response.getBody().author()).isNotNull();
        assertThat(response.getBody().genres()).isNotEmpty();
        assertThat(response.getBody().tropes()).isNotEmpty();
        assertThat(response.getBody().annotation()).isNotNull();
        assertThat(response.getBody().averageRating()).isEqualTo(4.5);
        assertThat(response.getBody().reviewCount()).isEqualTo(100);
        assertThat(response.getBody().ratingCount()).isEqualTo(200);
        verify(bookService).getById(bookId);
    }

    @Test
    @DisplayName("Should sort books by popularity (rating count)")
    void shouldSortBooksByPopularity() {
        // Arrange
        String title = null;
        List<Long> genreIds = null;
        int page = 0;
        int size = 10;
        String sort = "ratingCount,desc"; // Sort by popularity

        List<BookResponse> bookResponses = createTestBookResponses(10);
        Page<BookResponse> bookPage = new PageImpl<>(bookResponses);

        when(bookService.getAll(title, genreIds, page, size, sort)).thenReturn(bookPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(title, genreIds, page, size, sort);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(10);
        verify(bookService).getAll(title, genreIds, page, size, sort);
    }

    @Test
    @DisplayName("Should sort books by rating")
    void shouldSortBooksByRating() {
        // Arrange
        String title = null;
        List<Long> genreIds = null;
        int page = 0;
        int size = 10;
        String sort = "averageRating,desc"; // Sort by rating

        List<BookResponse> bookResponses = createTestBookResponses(10);
        Page<BookResponse> bookPage = new PageImpl<>(bookResponses);

        when(bookService.getAll(title, genreIds, page, size, sort)).thenReturn(bookPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(title, genreIds, page, size, sort);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(10);
        verify(bookService).getAll(title, genreIds, page, size, sort);
    }

    @Test
    @DisplayName("Should sort books by date added (newest first)")
    void shouldSortBooksByDateAdded() {
        // Arrange
        String title = null;
        List<Long> genreIds = null;
        int page = 0;
        int size = 10;
        String sort = "createdAt,desc"; // Sort by creation date

        List<BookResponse> bookResponses = createTestBookResponses(10);
        Page<BookResponse> bookPage = new PageImpl<>(bookResponses);

        when(bookService.getAll(title, genreIds, page, size, sort)).thenReturn(bookPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(title, genreIds, page, size, sort);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(10);
        verify(bookService).getAll(title, genreIds, page, size, sort);
    }

    @Test
    @DisplayName("Should sort books alphabetically")
    void shouldSortBooksAlphabetically() {
        // Arrange
        String title = null;
        List<Long> genreIds = null;
        int page = 0;
        int size = 10;
        String sort = "title,asc"; // Sort alphabetically

        List<BookResponse> bookResponses = createTestBookResponses(10);
        Page<BookResponse> bookPage = new PageImpl<>(bookResponses);

        when(bookService.getAll(title, genreIds, page, size, sort)).thenReturn(bookPage);

        // Act
        ResponseEntity<Page<BookResponse>> response = bookController.getAllBooks(title, genreIds, page, size, sort);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(10);
        verify(bookService).getAll(title, genreIds, page, size, sort);
    }
}