package ua.nure.readict.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "book")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "series_id")
    private Series series;

    @Column(name = "series_number")
    private Integer seriesNumber;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(name = "annotation", nullable = false, length = Integer.MAX_VALUE)
    private String annotation;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;

    @Column(name = "isbn", nullable = false, length = 20)
    private String isbn;

    @Column(name = "language", nullable = false, length = 50)
    private String language;

    @ColumnDefault("0")
    @Column(name = "average_rating", columnDefinition = "DECIMAL(3, 2)")
    private Double averageRating = 0.0;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "edition")
    private Integer edition;

    @ColumnDefault("0")
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @ColumnDefault("0")
    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "cover")
    private String cover;

    @ManyToMany(fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @JoinTable(name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @JoinTable(name = "book_trope",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "trope_id"))
    private Set<Trope> tropes = new LinkedHashSet<>();

}