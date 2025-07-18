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
public class ReviewId implements Serializable {
    @Serial
    private static final long serialVersionUID = -4610127238787066745L;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    public ReviewId(Long userId, Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public ReviewId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReviewId entity = (ReviewId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.bookId, entity.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, bookId);
    }

}