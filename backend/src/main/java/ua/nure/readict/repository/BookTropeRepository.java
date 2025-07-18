package ua.nure.readict.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.readict.entity.BookTrope;
import ua.nure.readict.entity.BookTropeId;

public interface BookTropeRepository extends JpaRepository<BookTrope, BookTropeId> {
}