package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.readict.entity.Series;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    boolean existsByName(String name);
}