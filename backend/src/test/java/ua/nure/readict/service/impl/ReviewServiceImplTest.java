package ua.nure.readict.service.impl;

import org.junit.jupiter.api.DisplayName;
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
import ua.nure.readict.dto.CreateReviewRequest;
import ua.nure.readict.dto.ReviewDto;
import ua.nure.readict.entity.*;
import ua.nure.readict.repository.RatingRepository;
import ua.nure.readict.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Captor
    private ArgumentCaptor<Review> reviewCaptor;

    @Test
    @DisplayName("Should return all reviews for a book with pagination")
    void shouldReturnAllReviewsForBookWithPagination() {
        // Arrange
        Long bookId = 1L;
        int page = 0;
        int size = 5;

        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Review review = new Review();
            ReviewId reviewId = new ReviewId();
            reviewId.setBookId(bookId);
            reviewId.setUserId((long) i);
            review.setId(reviewId);

            User user = new User();
            user.setId((long) i);
            user.setFirstName("First" + i);
            user.setLastName("Last" + i);
            review.setUser(user);

            review.setContent("Review content " + i);
            review.setAddedAt(LocalDateTime.now());

            reviews.add(review);
        }

        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size), 5);

        when(reviewRepository.findByIdBookIdOrderByAddedAtDesc(eq(bookId), any(Pageable.class)))
                .thenReturn(reviewPage);

        // Mock ratings for each review
        for (int i = 0; i < reviews.size(); i++) {
            Rating rating = new Rating();
            rating.setScore(i + 1); // Ratings from 1 to 5
            when(ratingRepository.findByUserIdAndBookId(
                    reviews.get(i).getUser().getId(), bookId))
                    .thenReturn(Optional.of(rating));
        }

        // Act
        Page<ReviewDto> result = reviewService.findAllByBookId(bookId, page, size);

        // Assert
        verify(reviewRepository).findByIdBookIdOrderByAddedAtDesc(eq(bookId), any(Pageable.class));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getContent().get(0).userId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).userName()).isEqualTo("First1 Last1");
        assertThat(result.getContent().get(0).rating()).isEqualTo(1);
        assertThat(result.getContent().get(0).content()).isEqualTo("Review content 1");
    }

    @Test
    @DisplayName("Should return review by user for a book")
    void shouldReturnReviewByUserForBook() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");

        CurrentUser currentUser = new CurrentUser(user, null);

        Review review = new Review();
        ReviewId reviewId = new ReviewId();
        reviewId.setBookId(bookId);
        reviewId.setUserId(userId);
        review.setId(reviewId);
        review.setUser(user);
        review.setContent("My review");
        review.setAddedAt(LocalDateTime.now());

        Rating rating = new Rating();
        rating.setScore(4);

        when(reviewRepository.findByIdUserIdAndIdBookId(userId, bookId))
                .thenReturn(Optional.of(review));
        when(ratingRepository.findByUserIdAndBookId(userId, bookId))
                .thenReturn(Optional.of(rating));

        // Act
        Optional<ReviewDto> result = reviewService.findReviewByUser(bookId, currentUser);

        // Assert
        verify(reviewRepository).findByIdUserIdAndIdBookId(userId, bookId);
        verify(ratingRepository).findByUserIdAndBookId(userId, bookId);

        assertThat(result).isPresent();
        assertThat(result.get().userId()).isEqualTo(userId);
        assertThat(result.get().userName()).isEqualTo("John Doe");
        assertThat(result.get().rating()).isEqualTo(4);
        assertThat(result.get().content()).isEqualTo("My review");
    }

    @Test
    @DisplayName("Should return empty when user has no review for book")
    void shouldReturnEmptyWhenUserHasNoReviewForBook() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");

        CurrentUser currentUser = new CurrentUser(user, null);

        when(reviewRepository.findByIdUserIdAndIdBookId(userId, bookId))
                .thenReturn(Optional.empty());

        // Act
        Optional<ReviewDto> result = reviewService.findReviewByUser(bookId, currentUser);

        // Assert
        verify(reviewRepository).findByIdUserIdAndIdBookId(userId, bookId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should create new review when none exists")
    void shouldCreateNewReviewWhenNoneExists() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");

        CurrentUser currentUser = new CurrentUser(user, null);

        CreateReviewRequest request = new CreateReviewRequest("This is a great book!");

        ReviewId reviewId = new ReviewId();
        reviewId.setBookId(bookId);
        reviewId.setUserId(userId);

        when(reviewRepository.findById(any(ReviewId.class))).thenReturn(Optional.empty());

        // Act
        reviewService.createOrUpdateReview(bookId, request, currentUser);

        // Assert
        verify(reviewRepository).findById(any(ReviewId.class));
        verify(reviewRepository).save(reviewCaptor.capture());

        Review capturedReview = reviewCaptor.getValue();
        assertThat(capturedReview.getId().getBookId()).isEqualTo(bookId);
        assertThat(capturedReview.getId().getUserId()).isEqualTo(userId);
        assertThat(capturedReview.getContent()).isEqualTo("This is a great book!");
        assertThat(capturedReview.getUser()).isEqualTo(user);
        assertThat(capturedReview.getAddedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update existing review")
    void shouldUpdateExistingReview() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");

        CurrentUser currentUser = new CurrentUser(user, null);

        CreateReviewRequest request = new CreateReviewRequest("Updated review content");

        Review existingReview = new Review();
        ReviewId reviewId = new ReviewId();
        reviewId.setBookId(bookId);
        reviewId.setUserId(userId);
        existingReview.setId(reviewId);
        existingReview.setUser(user);
        existingReview.setContent("Original content");
        existingReview.setAddedAt(LocalDateTime.now().minusDays(1));

        when(reviewRepository.findById(any(ReviewId.class))).thenReturn(Optional.of(existingReview));

        // Act
        reviewService.createOrUpdateReview(bookId, request, currentUser);

        // Assert
        verify(reviewRepository).findById(any(ReviewId.class));
        verify(reviewRepository).save(reviewCaptor.capture());

        Review capturedReview = reviewCaptor.getValue();
        assertThat(capturedReview.getId()).isEqualTo(reviewId);
        assertThat(capturedReview.getContent()).isEqualTo("Updated review content");
        assertThat(capturedReview.getUser()).isEqualTo(user);
        assertThat(capturedReview.getAddedAt()).isNotNull();
        // Verify the addedAt timestamp has been updated
        assertThat(capturedReview.getAddedAt()).isAfterOrEqualTo(existingReview.getAddedAt());
    }

    @Test
    @DisplayName("Should deleteBookFromLibrary review by user for a book")
    void shouldDeleteReviewByUserForBook() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        CurrentUser currentUser = new CurrentUser(user, null);

        // Act
        reviewService.deleteReview(bookId, currentUser);

        // Assert
        verify(reviewRepository).deleteById(any(ReviewId.class));
    }
}