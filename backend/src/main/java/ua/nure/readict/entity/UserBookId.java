package ua.nure.readict.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@DynamicUpdate
@Embeddable
public class UserBookId implements Serializable {
    private static final long serialVersionUID = 7347423981892076240L;
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserBookId entity = (UserBookId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.bookId, entity.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, bookId);
    }

}