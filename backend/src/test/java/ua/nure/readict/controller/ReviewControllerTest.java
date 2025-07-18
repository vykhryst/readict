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
import ua.nure.readict.dto.CreateReviewRequest;
import ua.nure.readict.dto.ReviewDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.entity.User;
import ua.nure.readict.service.impl.ReviewServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewServiceImpl reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    @DisplayName("Should return getAllReviewsByBook of reviews for a book")
    void shouldReturnListOfReviewsForBook() {
        // Arrange
        Long bookId = 1L;
        int page = 0;
        int size = 5;

        List<ReviewDto> reviews = List.of(
                new ReviewDto(1L, "User One", 5, "Great book!", LocalDateTime.now()),
                new ReviewDto(2L, "User Two", 4, "Very good read", LocalDateTime.now()),
                new ReviewDto(3L, "User Three", 3, "Decent book", LocalDateTime.now())
        );

        Page<ReviewDto> reviewPage = new PageImpl<>(reviews);

        when(reviewService.findAllByBookId(bookId, page, size)).thenReturn(reviewPage);

        // Act
        Page<ReviewDto> result = reviewController.getAllReviewsByBook(bookId, page, size);

        // Assert
        verify(reviewService).findAllByBookId(bookId, page, size);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).userId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).userName()).isEqualTo("User One");
        assertThat(result.getContent().get(0).rating()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return user's review for a book when exists")
    void shouldReturnUsersReviewForBookWhenExists() {
        // Arrange
        Long bookId = 1L;

        User user = new User();
        user.setId(1L);
        CurrentUser currentUser = new CurrentUser(user, null);

        ReviewDto reviewDto = new ReviewDto(
                1L, "John Doe", 4, "My review content", LocalDateTime.now());

        when(reviewService.findReviewByUser(bookId, currentUser)).thenReturn(Optional.of(reviewDto));

        // Act
        ResponseEntity<ReviewDto> response = reviewController.getMyReview(bookId, currentUser);

        // Assert
        verify(reviewService).findReviewByUser(bookId, currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().userId()).isEqualTo(1L);
        assertThat(response.getBody().userName()).isEqualTo("John Doe");
        assertThat(response.getBody().content()).isEqualTo("My review content");
    }

    @Test
    @DisplayName("Should return no content when user has no review for book")
    void shouldReturnNoContentWhenUserHasNoReviewForBook() {
        // Arrange
        Long bookId = 1L;

        User user = new User();
        user.setId(1L);
        CurrentUser currentUser = new CurrentUser(user, null);

        when(reviewService.findReviewByUser(bookId, currentUser)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ReviewDto> response = reviewController.getMyReview(bookId, currentUser);

        // Assert
        verify(reviewService).findReviewByUser(bookId, currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Should create or update review")
    void shouldCreateOrUpdateReview() {
        // Arrange
        Long bookId = 1L;

        User user = new User();
        user.setId(1L);
        CurrentUser currentUser = new CurrentUser(user, null);

        CreateReviewRequest request = new CreateReviewRequest("This is my review");

        // Act
        ResponseEntity<Void> response = reviewController.createOrUpdateReview(bookId, request, currentUser);

        // Assert
        verify(reviewService).createOrUpdateReview(bookId, request, currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should deleteBookFromLibrary review")
    void shouldDeleteReview() {
        // Arrange
        Long bookId = 1L;

        User user = new User();
        user.setId(1L);
        CurrentUser currentUser = new CurrentUser(user, null);

        // Act
        ResponseEntity<Void> response = reviewController.deleteReview(bookId, currentUser);

        // Assert
        verify(reviewService).deleteReview(bookId, currentUser);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}