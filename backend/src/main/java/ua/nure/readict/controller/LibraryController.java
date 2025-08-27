package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Library", description = "API for managing user's book library")
public class LibraryController {

    private final LibraryService service;

    @PutMapping("/{bookId}")
    @Operation(summary = "Move book to shelf", description = "Moves a specific book to the specified shelf")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book successfully moved"),
            @ApiResponse(responseCode = "404", description = "Book not found in user's library")
    })
    public ResponseEntity<Void> moveBookToShelf(
            @Parameter(description = "ID of the book to move") @PathVariable Long bookId,
            @Parameter(description = "Target shelf name") @RequestParam String shelf,
            @AuthenticationPrincipal CurrentUser cu) {

        service.moveBookToShelf(cu.getUser(), bookId, shelf);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Get book shelf", description = "Returns the shelf where the specified book is located")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shelf name returned"),
            @ApiResponse(responseCode = "204", description = "Book not found in user's library")
    })
    public ResponseEntity<String> getBookShelf(
            @Parameter(description = "ID of the book") @PathVariable Long bookId,
            @AuthenticationPrincipal CurrentUser cu) {

        return service.findShelf(cu.getUser().getId(), bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "Remove book from library", description = "Deletes the specified book from the user's library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book successfully removed"),
            @ApiResponse(responseCode = "404", description = "Book not found in user's library")
    })
    public ResponseEntity<Void> deleteBookFromLibrary(
            @Parameter(description = "ID of the book to remove") @PathVariable Long bookId,
            @AuthenticationPrincipal CurrentUser cu) {

        service.removeFromLibrary(cu.getUser().getId(), bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @Operation(summary = "Get library summary", description = "Returns a summary of the user's library, including book counts and other statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Library summary returned")
    })
    public LibrarySummaryDto getLibrarySummary(@AuthenticationPrincipal CurrentUser cu) {
        return service.getLibrarySummary(cu.getUser().getId());
    }

    @GetMapping
    @Operation(summary = "Get paginated list of library books", description = "Returns a paginated list of books in the user's library with optional filtering and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated list of books returned")
    })
    public Page<LibraryBookDto> getLibraryBookList(
            @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size, default is 20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort option, default is ADDED_AT_DESC") @RequestParam(defaultValue = "ADDED_AT_DESC") String sort,
            @Parameter(description = "Filter by shelf name") @RequestParam(required = false) String shelf,
            @Parameter(description = "Search by book title or author") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by rating") @RequestParam(required = false) Integer rating,
            @Parameter(description = "Filter by publication year from") @RequestParam(required = false) Integer yearFrom,
            @Parameter(description = "Filter by publication year to") @RequestParam(required = false) Integer yearTo,
            @Parameter(description = "Filter by genre IDs") @RequestParam(required = false) Set<Long> genres,
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
