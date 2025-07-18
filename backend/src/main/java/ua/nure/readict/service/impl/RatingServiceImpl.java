package ua.nure.readict.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ua.nure.readict.entity.Rating;
import ua.nure.readict.entity.RatingId;
import ua.nure.readict.event.RatingChangedEvent;
import ua.nure.readict.repository.RatingRepository;
import ua.nure.readict.service.interfaces.RatingService;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratings;
    private final ApplicationEventPublisher events;

    @Transactional
    public Integer findMyRating(Long userId, Long bookId) {
        return ratings.findByUserIdAndBookId(userId, bookId)
                .map(Rating::getScore)
                .orElse(null);
    }

    @Transactional
    public void setRating(Long userId, Long bookId, int score) {
        RatingId id = new RatingId(userId, bookId);
        Rating current = ratings.findById(id).orElse(null);

        if (current == null || current.getScore() != score) {
            ratings.save(new Rating(userId, bookId, score));
            events.publishEvent(new RatingChangedEvent(userId));
        }
    }

    @Transactional
    public void deleteRating(Long userId, Long bookId) {
        RatingId id = new RatingId(userId, bookId);
        if (ratings.existsById(id)) {
            ratings.deleteById(id);
            events.publishEvent(new RatingChangedEvent(userId));
        }
    }
}
