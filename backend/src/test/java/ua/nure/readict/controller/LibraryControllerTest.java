package ua.nure.readict.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.nure.readict.dto.LibraryBookDto;
import ua.nure.readict.dto.LibrarySummaryDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.entity.User;
import ua.nure.readict.service.impl.LibraryServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private LibraryServiceImpl libraryServiceImpl;

    @InjectMocks
    private LibraryController libraryController;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    private CurrentUser createCurrentUser() {
        return new CurrentUser(createTestUser(), null);
    }

    private LibraryBookDto createLibraryBookDto(Long id, String shelf) {
        return new LibraryBookDto(
                id,
                "Test Book " + id,
                "https://example.com/cover.jpg",
                "John Doe",
                1L,
                4.5,
                LocalDateTime.now(),
                shelf,
                "This is a test review",
                LocalDate.of(2023, 1, 1),
                Set.of(1L, 2L)
        );
    }

    @Test
    @DisplayName("Should add or move book to specified shelf")
    void shouldAddOrMoveBookToSpecifiedShelf() {
        // Arrange
        Long bookId = 1L;
        String shelfName = "WANT_TO_READ";
        CurrentUser currentUser = createCurrentUser();

        // Act
        ResponseEntity<Void> response = libraryController.moveBookToShelf(bookId, shelfName, currentUser);

        // Assert
        verify(libraryServiceImpl).moveBookToShelf(currentUser.getUser(), bookId, shelfName);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should removeFromLibrary book from user's library")
    void shouldRemoveBookFromUsersLibrary() {
        // Arrange
        Long bookId = 1L;
        CurrentUser currentUser = createCurrentUser();

        // Act
        ResponseEntity<Void> response = libraryController.deleteBookFromLibrary(bookId, currentUser);

        // Assert
        verify(libraryServiceImpl).removeFromLibrary(currentUser.getUser().getId(), bookId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should return library getLibrarySummary")
    void shouldReturnLibrarySummary() {
        // Arrange
        CurrentUser currentUser = createCurrentUser();
        LibrarySummaryDto summaryDto = new LibrarySummaryDto(10, 5, 2, 3);

        when(libraryServiceImpl.getLibrarySummary(currentUser.getUser().getId())).thenReturn(summaryDto);

        // Act
        LibrarySummaryDto result = libraryController.getLibrarySummary(currentUser);

        // Assert
        verify(libraryServiceImpl).getLibrarySummary(currentUser.getUser().getId());

        assertThat(result).isNotNull();
        assertThat(result.total()).isEqualTo(10);
        assertThat(result.read()).isEqualTo(5);
        assertThat(result.reading()).isEqualTo(2);
        assertThat(result.want()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return getAllReviewsByBook of books in library with default parameters")
    void shouldReturnListOfBooksInLibraryWithDefaultParameters() {
        // Arrange
        int page = 0;
        int size = 20;
        String sort = "ADDED_AT_DESC";
        CurrentUser currentUser = createCurrentUser();

        List<LibraryBookDto> books = List.of(
                createLibraryBookDto(1L, "READ"),
                createLibraryBookDto(2L, "CURRENTLY_READING"),
                createLibraryBookDto(3L, "WANT_TO_READ")
        );
        Page<LibraryBookDto> bookPage = new PageImpl<>(books);

        when(libraryServiceImpl.findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        Page<LibraryBookDto> result = libraryController.getLibraryBookList(
                page, size, sort, null, null, null, null, null, null, currentUser);

        // Assert
        verify(libraryServiceImpl).findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("Should filter library books by shelf")
    void shouldFilterLibraryBooksByShelf() {
        // Arrange
        int page = 0;
        int size = 20;
        String sort = "ADDED_AT_DESC";
        String shelf = "READ";
        CurrentUser currentUser = createCurrentUser();

        List<LibraryBookDto> books = List.of(
                createLibraryBookDto(1L, shelf),
                createLibraryBookDto(2L, shelf)
        );
        Page<LibraryBookDto> bookPage = new PageImpl<>(books);

        when(libraryServiceImpl.findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                eq(shelf),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        Page<LibraryBookDto> result = libraryController.getLibraryBookList(
                page, size, sort, shelf, null, null, null, null, null, currentUser);

        // Assert
        verify(libraryServiceImpl).findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                eq(shelf),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).shelf()).isEqualTo(shelf);
        assertThat(result.getContent().get(1).shelf()).isEqualTo(shelf);
    }

    @Test
    @DisplayName("Should filter library books by search term")
    void shouldFilterLibraryBooksBySearchTerm() {
        // Arrange
        int page = 0;
        int size = 20;
        String sort = "ADDED_AT_DESC";
        String search = "fantasy";
        CurrentUser currentUser = createCurrentUser();

        List<LibraryBookDto> books = List.of(
                createLibraryBookDto(1L, "READ"),
                createLibraryBookDto(2L, "WANT_TO_READ")
        );
        Page<LibraryBookDto> bookPage = new PageImpl<>(books);

        when(libraryServiceImpl.findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                eq(search),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        Page<LibraryBookDto> result = libraryController.getLibraryBookList(
                page, size, sort, null, search, null, null, null, null, currentUser);

        // Assert
        verify(libraryServiceImpl).findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                eq(search),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should filter library books by minimum rating")
    void shouldFilterLibraryBooksByMinimumRating() {
        // Arrange
        int page = 0;
        int size = 20;
        String sort = "ADDED_AT_DESC";
        Integer rating = 4;
        CurrentUser currentUser = createCurrentUser();

        List<LibraryBookDto> books = List.of(
                createLibraryBookDto(1L, "READ"),
                createLibraryBookDto(2L, "READ")
        );
        Page<LibraryBookDto> bookPage = new PageImpl<>(books);

        when(libraryServiceImpl.findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                eq(rating),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        Page<LibraryBookDto> result = libraryController.getLibraryBookList(
                page, size, sort, null, null, rating, null, null, null, currentUser);

        // Assert
        verify(libraryServiceImpl).findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                eq(rating),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should filter library books by publication year range")
    void shouldFilterLibraryBooksByPublicationYearRange() {
        // Arrange
        int page = 0;
        int size = 20;
        String sort = "ADDED_AT_DESC";
        Integer yearFrom = 2020;
        Integer yearTo = 2023;
        CurrentUser currentUser = createCurrentUser();

        List<LibraryBookDto> books = List.of(
                createLibraryBookDto(1L, "READ"),
                createLibraryBookDto(2L, "READ")
        );
        Page<LibraryBookDto> bookPage = new PageImpl<>(books);

        when(libraryServiceImpl.findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(yearFrom, 1, 1)),
                eq(LocalDate.of(yearTo, 12, 31)),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        Page<LibraryBookDto> result = libraryController.getLibraryBookList(
                page, size, sort, null, null, null, yearFrom, yearTo, null, currentUser);

        // Assert
        verify(libraryServiceImpl).findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(yearFrom, 1, 1)),
                eq(LocalDate.of(yearTo, 12, 31)),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should filter library books by genres")
    void shouldFilterLibraryBooksByGenres() {
        // Arrange
        int page = 0;
        int size = 20;
        String sort = "ADDED_AT_DESC";
        Set<Long> genres = Set.of(1L, 2L);
        CurrentUser currentUser = createCurrentUser();

        List<LibraryBookDto> books = List.of(
                createLibraryBookDto(1L, "READ"),
                createLibraryBookDto(2L, "READ")
        );
        Page<LibraryBookDto> bookPage = new PageImpl<>(books);

        when(libraryServiceImpl.findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(genres),
                eq(sort),
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        Page<LibraryBookDto> result = libraryController.getLibraryBookList(
                page, size, sort, null, null, null, null, null, genres, currentUser);

        // Assert
        verify(libraryServiceImpl).findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(genres),
                eq(sort),
                any(PageRequest.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should sort library books by specified criteria")
    void shouldSortLibraryBooksBySpecifiedCriteria() {
        // Arrange
        int page = 0;
        int size = 20;
        String sort = "TITLE_ASC";
        CurrentUser currentUser = createCurrentUser();

        List<LibraryBookDto> books = List.of(
                createLibraryBookDto(1L, "READ"),
                createLibraryBookDto(2L, "READ"),
                createLibraryBookDto(3L, "READ")
        );
        Page<LibraryBookDto> bookPage = new PageImpl<>(books);

        when(libraryServiceImpl.findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        Page<LibraryBookDto> result = libraryController.getLibraryBookList(
                page, size, sort, null, null, null, null, null, null, currentUser);

        // Assert
        verify(libraryServiceImpl).findAllInUserLibrary(
                eq(currentUser.getUser().getId()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(sort),
                any(PageRequest.class)
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
    }
}