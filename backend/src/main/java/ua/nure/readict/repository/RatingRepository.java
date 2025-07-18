package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.readict.entity.Rating;
import ua.nure.readict.entity.RatingId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {


    List<Rating> findAllByUserIdIn(Set<Long> neighborIds);

    List<Rating> findAllByUserId(Long userId);

    @Query("""
               select r
               from Rating r
               where r.userId in :ids
            """)
    List<Rating> findAllByUserIds(@Param("ids") Collection<Long> ids);

    Optional<Rating> findByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT r.score FROM Rating r WHERE r.userId = :uid AND r.bookId = :bid")
    Optional<Integer> findScore(@Param("uid") Long uid, @Param("bid") Long bid);

}