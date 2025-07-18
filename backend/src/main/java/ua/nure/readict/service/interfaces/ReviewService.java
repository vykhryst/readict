package ua.nure.readict.service.interfaces;

import org.springframework.data.domain.Page;
import ua.nure.readict.dto.CreateReviewRequest;
import ua.nure.readict.dto.ReviewDto;
import ua.nure.readict.entity.CurrentUser;

import java.util.Optional;

public interface ReviewService {
    Page<ReviewDto> findAllByBookId(Long bookId, int page, int size);

    Optional<ReviewDto> findReviewByUser(Long bookId, CurrentUser cu);

    void createOrUpdateReview(Long bookId, CreateReviewRequest rq, CurrentUser cu);

    void deleteReview(Long bookId, CurrentUser cu);

}
