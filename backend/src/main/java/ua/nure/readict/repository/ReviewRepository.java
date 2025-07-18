package ua.nure.readict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.readict.entity.Review;
import ua.nure.readict.entity.ReviewId;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
    /**
     * пагінований список усіх відгуків по книжці
     */
    Page<Review> findByIdBookIdOrderByAddedAtDesc(Long bookId, Pageable pg);

    /**
     * відгук поточного юзера (може й не бути)
     */
    Optional<Review> findByIdUserIdAndIdBookId(Long userId, Long bookId);
}