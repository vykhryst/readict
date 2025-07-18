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
public class BookTropeId implements Serializable {
    @Serial
    private static final long serialVersionUID = 5236513198983775976L;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "trope_id", nullable = false)
    private Long tropeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BookTropeId entity = (BookTropeId) o;
        return Objects.equals(this.tropeId, entity.tropeId) &&
                Objects.equals(this.bookId, entity.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tropeId, bookId);
    }

}