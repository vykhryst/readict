package ua.nure.readict.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class BookGenreId implements Serializable {
    @Serial
    private static final long serialVersionUID = -7178153157369766840L;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "genre_id", nullable = false)
    private Long genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BookGenreId entity = (BookGenreId) o;
        return Objects.equals(this.genreId, entity.genreId) &&
                Objects.equals(this.bookId, entity.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreId, bookId);
    }

}