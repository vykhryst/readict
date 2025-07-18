package ua.nure.readict.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.nure.readict.dto.RatingRequest;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.entity.User;
import ua.nure.readict.service.interfaces.RatingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

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

    @Test
    @DisplayName("Should return user's rating for a book")
    void shouldReturnUsersRatingForBook() {
        // Arrange
        Long bookId = 1L;
        Integer rating = 4;
        CurrentUser currentUser = createCurrentUser();

        when(ratingService.findMyRating(currentUser.getUser().getId(), bookId)).thenReturn(rating);

        // Act
        ResponseEntity<Integer> response = ratingController.getMyRating(bookId, currentUser);

        // Assert
        verify(ratingService).findMyRating(currentUser.getUser().getId(), bookId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(rating);
    }

    @Test
    @DisplayName("Should return no content when user has no rating for a book")
    void shouldReturnNoContentWhenUserHasNoRatingForBook() {
        // Arrange
        Long bookId = 1L;
        CurrentUser currentUser = createCurrentUser();

        when(ratingService.findMyRating(currentUser.getUser().getId(), bookId)).thenReturn(null);

        // Act
        ResponseEntity<Integer> response = ratingController.getMyRating(bookId, currentUser);

        // Assert
        verify(ratingService).findMyRating(currentUser.getUser().getId(), bookId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Should set rating for a book")
    void shouldSetRatingForBook() {
        // Arrange
        Long bookId = 1L;
        Integer score = 5;
        RatingRequest request = new RatingRequest(score);
        CurrentUser currentUser = createCurrentUser();

        // Act
        ResponseEntity<Void> response = ratingController.rateBook(bookId, request, currentUser);

        // Assert
        verify(ratingService).setRating(currentUser.getUser().getId(), bookId, score);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should deleteBookFromLibrary rating for a book")
    void shouldDeleteRatingForBook() {
        // Arrange
        Long bookId = 1L;
        CurrentUser currentUser = createCurrentUser();

        // Act
        ResponseEntity<Void> response = ratingController.deleteRatingForBook(bookId, currentUser);

        // Assert
        verify(ratingService).deleteRating(currentUser.getUser().getId(), bookId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}