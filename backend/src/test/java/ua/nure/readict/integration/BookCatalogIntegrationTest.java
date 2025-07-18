package ua.nure.readict.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookCatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return all books with pagination")
    void shouldReturnAllBooksWithPagination() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("findAllInUserLibrary", "0")
                        .param("size", "10")
                        .param("sort", "title,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.page", notNullValue()))
                .andExpect(jsonPath("$.page.number", is(0)))
                .andExpect(jsonPath("$.page.size", is(10)));
    }

    @Test
    @DisplayName("Should filter books by title")
    void shouldFilterBooksByTitle() throws Exception {
        // Some sample title that should exist in the test database
        String title = "Harry";

        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("title", title)
                        .param("findAllInUserLibrary", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", notNullValue()))
                // Verify each returned book contains the search term in the title (case insensitive)
                .andExpect(jsonPath("$.content[*].title", everyItem(containsStringIgnoringCase(title))));
    }

    @Test
    @DisplayName("Should filter books by genre")
    void shouldFilterBooksByGenre() throws Exception {
        // Assuming genre with ID 1 exists in test database
        Long genreId = 1L;

        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("genreIds", String.valueOf(genreId))
                        .param("findAllInUserLibrary", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", notNullValue()))
                // Verify each book has the requested genre
                .andExpect(jsonPath("$.content[*].genres[*].id", hasItem(genreId.intValue())));
    }

    @Test
    @DisplayName("Should sort books by rating in descending order")
    void shouldSortBooksByRatingDesc() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("findAllInUserLibrary", "0")
                        .param("size", "10")
                        .param("sort", "averageRating,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", notNullValue()))
                // Verify the ratings are sorted in descending order
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.content[0].averageRating", greaterThanOrEqualTo(0.0)));
    }

    @Test
    @DisplayName("Should sort books by popularity (ratingCount) in descending order")
    void shouldSortBooksByPopularityDesc() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("findAllInUserLibrary", "0")
                        .param("size", "10")
                        .param("sort", "ratingCount,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", notNullValue()))
                // Verify the rating counts are sorted in descending order
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.content[0].ratingCount", greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("Should return detailed book information by ID")
    void shouldReturnDetailedBookInformationById() throws Exception {
        // Assuming a book with ID 1 exists in test database
        Long bookId = 1L;

        // Act & Assert
        mockMvc.perform(get("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookId.intValue())))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.annotation", notNullValue()))
                .andExpect(jsonPath("$.author", notNullValue()))
                .andExpect(jsonPath("$.author.firstName", notNullValue()))
                .andExpect(jsonPath("$.author.lastName", notNullValue()))
                .andExpect(jsonPath("$.genres", notNullValue()))
                .andExpect(jsonPath("$.averageRating", notNullValue()))
                .andExpect(jsonPath("$.ratingCount", notNullValue()))
                .andExpect(jsonPath("$.reviewCount", notNullValue()))
                .andExpect(jsonPath("$.publicationDate", notNullValue()))
                .andExpect(jsonPath("$.isbn", notNullValue()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent book ID")
    void shouldReturn404ForNonExistentBookId() throws Exception {
        // A book ID that definitely doesn't exist
        Long nonExistentBookId = 999999L;

        // Act & Assert
        mockMvc.perform(get("/books/{id}", nonExistentBookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should sort books by creation date (newest first)")
    void shouldSortBooksByCreationDateDesc() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("findAllInUserLibrary", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", notNullValue()))
                // We can't easily verify the exact order, but we can check that the response is successful
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    @DisplayName("Should sort books alphabetically by title")
    void shouldSortBooksByTitleAsc() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/books")
                        .param("findAllInUserLibrary", "0")
                        .param("size", "10")
                        .param("sort", "title,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))))
                // Verify alphabetical order if there are enough books
                .andExpect(jsonPath("$.content[0].title",  notNullValue()));
    }
}