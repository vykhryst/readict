package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.readict.entity.Shelf;

import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    Optional<Shelf> findByName(String name);
}