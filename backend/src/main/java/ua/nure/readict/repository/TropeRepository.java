package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.readict.entity.Trope;

public interface TropeRepository extends JpaRepository<Trope, Long> {
    boolean existsByName(String name);
}