package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.nure.readict.entity.Genre;
import ua.nure.readict.entity.User;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select u.favouriteGenres from User u where u.id = :id")
    Set<Genre> findFavouriteGenresById(Long id);

    @Query("""
               select g.id
               from User u join u.favouriteGenres g
               where u.id = :id
            """)
    Set<Long> findFavouriteGenreIds(@Param("id") Long id);

    Boolean existsByEmail(String email);
}