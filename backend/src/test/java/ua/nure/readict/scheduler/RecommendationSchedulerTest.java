package ua.nure.readict.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.readict.entity.Rating;
import ua.nure.readict.recommendation.RecommendationService;
import ua.nure.readict.repository.RatingRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationSchedulerTest {

    @Mock
    private RecommendationService recommendationService;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RecommendationScheduler scheduler;

    @Test
    @DisplayName("Should compute recommendations for all users with ratings")
    void shouldComputeRecommendationsForAllUsersWithRatings() {
        // Arrange
        Rating rating1 = new Rating();
        rating1.setUserId(1L);

        Rating rating2 = new Rating();
        rating2.setUserId(2L);

        Rating rating3 = new Rating();
        rating3.setUserId(1L); // same user as rating1

        List<Rating> ratings = List.of(rating1, rating2, rating3);


        when(ratingRepository.findAll()).thenReturn(ratings);

        // Act
        scheduler.computeAllUsersRecommendations();

        // Assert
        verify(ratingRepository).findAll();

        // Verify that refreshRecommendationsForUser was called for each unique user ID
        verify(recommendationService).refreshRecommendationsForUser(1L);
        verify(recommendationService).refreshRecommendationsForUser(2L);

        // Verify no other interactions
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    @DisplayName("Should not compute recommendations when no users have ratings")
    void shouldNotComputeRecommendationsWhenNoUsersHaveRatings() {
        // Arrange
        List<Rating> emptyRatings = List.of();

        when(ratingRepository.findAll()).thenReturn(emptyRatings);

        // Act
        scheduler.computeAllUsersRecommendations();

        // Assert
        verify(ratingRepository).findAll();

        // Verify recommendationService was not called
        verifyNoInteractions(recommendationService);
    }
}