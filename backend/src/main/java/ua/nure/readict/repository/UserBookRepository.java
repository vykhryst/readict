package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.readict.entity.UserBook;
import ua.nure.readict.entity.UserBookId;

import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, UserBookId>, JpaSpecificationExecutor<UserBook> {
    Optional<UserBook> findByUserIdAndBookId(Long userId, Long bookId);

    void deleteByUserIdAndBookId(Long userId, Long bookId);

    /* маленька статистика */
    @Query("select count(ub) from UserBook ub where ub.user.id = :uid")
    long countAll(@Param("uid") Long uid);

    @Query("""
            select count(ub) from UserBook ub
            where ub.user.id = :uid and ub.shelf.name = :shelf
            """)
    long countShelf(@Param("uid") Long uid, @Param("shelf") String shelf);

}