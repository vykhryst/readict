package ua.nure.readict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ua.nure.readict.entity.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long>, JpaSpecificationExecutor<Genre> {
    Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByName(String name);
}