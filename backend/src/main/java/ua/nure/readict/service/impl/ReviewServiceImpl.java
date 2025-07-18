package ua.nure.readict.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.CreateReviewRequest;
import ua.nure.readict.dto.ReviewDto;
import ua.nure.readict.entity.*;
import ua.nure.readict.repository.RatingRepository;
import ua.nure.readict.repository.ReviewRepository;
import ua.nure.readict.service.interfaces.ReviewService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviews;
    private final RatingRepository ratings;

    public Page<ReviewDto> findAllByBookId(Long bookId, int page, int size) {
        return reviews
                .findByIdBookIdOrderByAddedAtDesc(bookId, PageRequest.of(page, size))
                .map(r -> new ReviewDto(
                        r.getUser().getId(),
                        r.getUser().getFirstName() + " " + r.getUser().getLastName(),
                        ratings.findByUserIdAndBookId(r.getUser().getId(), bookId)
                                .map(Rating::getScore)
                                .orElse(0),
                        r.getContent(),
                        r.getAddedAt()));
    }

    public Optional<ReviewDto> findReviewByUser(Long bookId, CurrentUser cu) {
        Long userId = cu.getUser().getId();
        return reviews.findByIdUserIdAndIdBookId(userId, bookId)
                .map(r -> new ReviewDto(
                        userId,
                        cu.getUser().getFirstName() + " " + cu.getUser().getLastName(),
                        ratings.findByUserIdAndBookId(userId, bookId)
                                .map(Rating::getScore)
                                .orElse(0),
                        r.getContent(),
                        r.getAddedAt()));
    }

    public void createOrUpdateReview(Long bookId, CreateReviewRequest rq, CurrentUser cu) {
        ReviewId id = new ReviewId();
        id.setUserId(cu.getUser().getId());
        id.setBookId(bookId);

        Review rev = reviews.findById(id).orElseGet(() -> {
            Review n = new Review();
            n.setId(id);
            n.setBook(new Book() {{
                setId(bookId);
            }});
            n.setUser(cu.getUser());
            return n;
        });
        rev.setContent(rq.content());
        rev.setAddedAt(LocalDateTime.now());

        reviews.save(rev);
    }

    public void deleteReview(Long bookId, CurrentUser cu) {
        ReviewId id = new ReviewId();
        id.setUserId(cu.getUser().getId());
        id.setBookId(bookId);
        reviews.deleteById(id);
    }
}