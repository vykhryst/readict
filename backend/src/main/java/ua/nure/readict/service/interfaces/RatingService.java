package ua.nure.readict.service.interfaces;

import ua.nure.readict.dto.RatingDto;

public interface RatingService {
    public Integer findMyRating(Long userId, Long bookId);
    public void setRating(Long userId, Long bookId, int score);
    public void deleteRating(Long userId, Long bookId);
}
