package ua.nure.readict.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.LibraryBookDto;
import ua.nure.readict.dto.LibrarySummaryDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.service.interfaces.LibraryService;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService service;

    @PutMapping("/{bookId}")
    public ResponseEntity<Void> moveBookToShelf(@PathVariable Long bookId,
                                                @RequestParam String shelf,
                                                @AuthenticationPrincipal CurrentUser cu) {
        service.moveBookToShelf(cu.getUser(), bookId, shelf);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<String> getBookShelf(@PathVariable Long bookId,
                                               @AuthenticationPrincipal CurrentUser cu) {
        return service.findShelf(cu.getUser().getId(), bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBookFromLibrary(@PathVariable Long bookId,
                                                      @AuthenticationPrincipal CurrentUser cu) {
        service.removeFromLibrary(cu.getUser().getId(), bookId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/summary")
    public LibrarySummaryDto getLibrarySummary(@AuthenticationPrincipal CurrentUser cu) {
        return service.getLibrarySummary(cu.getUser().getId());
    }

    @GetMapping
    public Page<LibraryBookDto> getLibraryBookList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ADDED_AT_DESC") String sort,
            @RequestParam(required = false) String shelf,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Set<Long> genres,
            @AuthenticationPrincipal CurrentUser cu) {

        return service.findAllInUserLibrary(
                cu.getUser().getId(),
                shelf,
                search,
                rating,
                yearFrom != null ? LocalDate.of(yearFrom, 1, 1) : null,
                yearTo != null ? LocalDate.of(yearTo, 12, 31) : null,
                genres,
                sort,
                PageRequest.of(page, size)
        );
    }
}
