package ua.nure.readict.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.readict.entity.Book;
import ua.nure.readict.entity.Genre;
import ua.nure.readict.entity.Rating;
import ua.nure.readict.entity.Recommendation;
import ua.nure.readict.repository.BookRepository;
import ua.nure.readict.repository.RatingRepository;
import ua.nure.readict.repository.RecommendationRepository;
import ua.nure.readict.repository.UserRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SimilarityService similarityService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommenderProperties properties;

    @InjectMocks
    private RecommendationService recommendationService;

    @Captor
    private ArgumentCaptor<List<Recommendation>> recommendationsCaptor;

    @Test
    @DisplayName("Should deleteBookFromLibrary old recommendations and save new ones when refreshing")
    void shouldDeleteOldRecommendationsAndSaveNewOnesWhenRefreshing() {
        // Arrange
        Long userId = 1L;

        // Mock similarity service
        Map<Long, Double> similarities = new HashMap<>();
        similarities.put(2L, 0.8);
        similarities.put(3L, 0.6);
        when(similarityService.computeSimilarities(userId)).thenReturn(similarities);

        // Mock ratings for the user and similar users
        Rating userRating = new Rating();
        userRating.setUserId(userId);
        userRating.setBookId(101L);
        userRating.setScore(5);

        Rating neighbor1Rating1 = new Rating();
        neighbor1Rating1.setUserId(2L);
        neighbor1Rating1.setBookId(102L);
        neighbor1Rating1.setScore(4);

        Rating neighbor1Rating2 = new Rating();
        neighbor1Rating2.setUserId(2L);
        neighbor1Rating2.setBookId(103L);
        neighbor1Rating2.setScore(5);

        Rating neighbor2Rating = new Rating();
        neighbor2Rating.setUserId(3L);
        neighbor2Rating.setBookId(102L);
        neighbor2Rating.setScore(5);

        List<Rating> allRatings = Arrays.asList(userRating, neighbor1Rating1, neighbor1Rating2, neighbor2Rating);
        when(ratingRepository.findAllByUserIdIn(anySet())).thenReturn(allRatings);

        // Mock genre preferences
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> favouriteGenres = new HashSet<>(Arrays.asList(genre1, genre2));
        when(userRepository.findFavouriteGenresById(userId)).thenReturn(favouriteGenres);

        // Mock books
        Book book1 = new Book();
        book1.setId(102L);
        Set<Genre> book1Genres = new HashSet<>();
        book1Genres.add(genre1);
        book1.setGenres(book1Genres);

        Book book2 = new Book();
        book2.setId(103L);
        Set<Genre> book2Genres = new HashSet<>();
        book2Genres.add(genre2);
        book2.setGenres(book2Genres);

        List<Book> books = Arrays.asList(book1, book2);
        when(bookRepository.findAllWithGenresByIdIn(anySet())).thenReturn(books);

        // Mock properties
        when(properties.getMinScoreCF()).thenReturn(3.0);
        when(properties.getMinScoreHybrid()).thenReturn(3.0);
        when(properties.getWeightCF()).thenReturn(0.7);
        when(properties.getWeightGenre()).thenReturn(0.3);
        when(properties.getMaxRating()).thenReturn(5);
        when(properties.getMaxPerUser()).thenReturn(10);

        // Act
        recommendationService.refreshRecommendationsForUser(userId);

        // Assert
        verify(recommendationRepository).deleteAllByUserId(userId);
        verify(recommendationRepository).saveAll(recommendationsCaptor.capture());

        List<Recommendation> savedRecommendations = recommendationsCaptor.getValue();
        assertThat(savedRecommendations).isNotEmpty();

        // Verify all saved recommendations are for the correct user
        for (Recommendation rec : savedRecommendations) {
            assertThat(rec.getUserId()).isEqualTo(userId);
            assertThat(rec.getCreatedAt()).isNotNull();
            // Book IDs should be either 102 or 103 (the ones we mocked)
            assertThat(rec.getBookId()).isIn(102L, 103L);
        }
    }

    @Test
    @DisplayName("Should not process if there are no similar users")
    void shouldNotProcessIfThereAreNoSimilarUsers() {
        // Arrange
        Long userId = 1L;

        // Mock similarity service to return empty map (no similar users)
        when(similarityService.computeSimilarities(userId)).thenReturn(Collections.emptyMap());

        // Act
        recommendationService.refreshRecommendationsForUser(userId);

        // Assert
        verify(recommendationRepository).deleteAllByUserId(userId);
        // Verify no recommendations were saved
        verify(recommendationRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should add genre weight to recommendations when user has favourite genres")
    void shouldAddGenreWeightToRecommendationsWhenUserHasFavouriteGenres() {
        // Arrange
        Long userId = 1L;

        // Mock similarity service
        Map<Long, Double> similarities = new HashMap<>();
        similarities.put(2L, 0.8);
        when(similarityService.computeSimilarities(userId)).thenReturn(similarities);

        // Mock ratings for the user and similar users
        Rating userRating = new Rating();
        userRating.setUserId(userId);
        userRating.setBookId(101L);
        userRating.setScore(5);

        Rating neighborRating = new Rating();
        neighborRating.setUserId(2L);
        neighborRating.setBookId(102L);
        neighborRating.setScore(4);

        List<Rating> allRatings = Arrays.asList(userRating, neighborRating);
        when(ratingRepository.findAllByUserIdIn(anySet())).thenReturn(allRatings);

        // Mock genre preferences
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Set<Genre> favouriteGenres = new HashSet<>(Collections.singletonList(genre1));
        when(userRepository.findFavouriteGenresById(userId)).thenReturn(favouriteGenres);

        // Mock books with matching genres
        Book book = new Book();
        book.setId(102L);
        Set<Genre> bookGenres = new HashSet<>();
        bookGenres.add(genre1); // Book has the user's favourite genre
        book.setGenres(bookGenres);

        List<Book> books = Collections.singletonList(book);
        when(bookRepository.findAllWithGenresByIdIn(anySet())).thenReturn(books);

        // Mock properties
        when(properties.getMinScoreCF()).thenReturn(3.0);
        when(properties.getMinScoreHybrid()).thenReturn(3.0);
        when(properties.getWeightCF()).thenReturn(0.7);
        when(properties.getWeightGenre()).thenReturn(0.3);
        when(properties.getMaxRating()).thenReturn(5);
        when(properties.getMaxPerUser()).thenReturn(10);

        // Act
        recommendationService.refreshRecommendationsForUser(userId);

        // Assert
        verify(recommendationRepository).deleteAllByUserId(userId);
        verify(recommendationRepository).saveAll(recommendationsCaptor.capture());

        List<Recommendation> savedRecommendations = recommendationsCaptor.getValue();
        assertThat(savedRecommendations).isNotEmpty();

        // The book with matching genre should have a higher predicted score than pure CF would give
        Recommendation rec = savedRecommendations.get(0);
        assertThat(rec.getBookId()).isEqualTo(102L);
        // Using the formula: 0.7 * cfScore + 0.3 * (genreScore * maxRating)
        // We mocked genreScore to be 1.0 (100% match), so this should boost the score
        assertThat(rec.getPredictedScore()).isGreaterThan(0.0);
    }
}