package ua.nure.readict.listener;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.readict.event.FavouriteGenresChangedEvent;
import ua.nure.readict.event.RatingChangedEvent;
import ua.nure.readict.recommendation.RecommendationService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationEventsListenerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationEventsListener listener;

    @Test
    @DisplayName("Should trigger recommendation refresh when rating is changed")
    void shouldTriggerRecommendationRefreshWhenRatingIsChanged() {
        // Arrange
        Long userId = 1L;
        RatingChangedEvent event = new RatingChangedEvent(userId);

        // Act
        listener.onRatingChanged(event);

        // Assert
        verify(recommendationService).refreshRecommendationsForUser(userId);
    }

    @Test
    @DisplayName("Should trigger recommendation refresh when favourite genres are changed")
    void shouldTriggerRecommendationRefreshWhenFavouriteGenresAreChanged() {
        // Arrange
        Long userId = 1L;
        FavouriteGenresChangedEvent event = new FavouriteGenresChangedEvent(userId);

        // Act
        listener.onGenresChanged(event);

        // Assert
        verify(recommendationService).refreshRecommendationsForUser(userId);
    }
}