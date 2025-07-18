package ua.nure.readict.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.nure.readict.dto.LibraryBookDto;
import ua.nure.readict.dto.LibrarySummaryDto;
import ua.nure.readict.entity.User;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface LibraryService {
    LibrarySummaryDto getLibrarySummary(Long uid);

    Page<LibraryBookDto> findAllInUserLibrary(Long uid,
                                              String shelf,
                                              String search,
                                              Integer rating,
                                              LocalDate yearFrom,
                                              LocalDate yearTo,
                                              Set<Long> genres,
                                              String sortCode,
                                              Pageable pageable);

    Optional<String> findShelf(Long userId, Long bookId);

    void moveBookToShelf(User user, Long bookId, String shelfName);

    void removeFromLibrary(Long userId, Long bookId);
}
