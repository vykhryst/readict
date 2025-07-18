DROP TABLE IF EXISTS book_genre;
DROP TABLE IF EXISTS book_trope;
DROP TABLE IF EXISTS user_book;
DROP TABLE IF EXISTS recommendation;
DROP TABLE IF EXISTS favourite_genre;
DROP TABLE IF EXISTS rating;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS series;
DROP TABLE IF EXISTS shelf;
DROP TABLE IF EXISTS trope;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS role;


-- Table for user roles
CREATE TABLE IF NOT EXISTS role
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Table for users
CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    role_id       BIGINT       NOT NULL REFERENCES role (id),
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL
);

-- Table for book series
CREATE TABLE IF NOT EXISTS series
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Table for authors
CREATE TABLE IF NOT EXISTS author
(
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    last_name   VARCHAR(255) NOT NULL,
    biography   TEXT
);

-- Table for books
CREATE TABLE IF NOT EXISTS book
(
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    series_id        BIGINT       REFERENCES series (id) ON DELETE SET NULL,
    series_number    INTEGER,
    author_id        BIGINT       NOT NULL REFERENCES author (id),
    annotation       TEXT         NOT NULL,
    page_count       INTEGER      NOT NULL CHECK (page_count > 0),
    publication_date DATE         NOT NULL,
    isbn             VARCHAR(20)  NOT NULL UNIQUE,
    language         VARCHAR(50)  NOT NULL,
    average_rating   DECIMAL(3, 2) DEFAULT 0 CHECK (average_rating >= 0 AND average_rating <= 5),
    publisher        VARCHAR(255),
    edition          INTEGER,
    review_count     INTEGER       DEFAULT 0 CHECK (review_count >= 0),
    rating_count     INTEGER       DEFAULT 0 CHECK (rating_count >= 0),
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    cover            VARCHAR(255)
);

-- Table for genres
CREATE TABLE IF NOT EXISTS genre
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Table for tropes
CREATE TABLE IF NOT EXISTS trope
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Table for book-trope associations
CREATE TABLE IF NOT EXISTS book_trope
(
    book_id  BIGINT NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    trope_id BIGINT NOT NULL REFERENCES trope (id),
    PRIMARY KEY (book_id, trope_id)
);

-- Table for book-genre associations
CREATE TABLE IF NOT EXISTS book_genre
(
    book_id  BIGINT NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genre (id),
    PRIMARY KEY (book_id, genre_id)
);

-- Table for reviews
CREATE TABLE IF NOT EXISTS review
(
    user_id  BIGINT NOT NULL REFERENCES users (id),
    book_id  BIGINT NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    content  TEXT   NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, book_id)
);

-- Table for ratings
CREATE TABLE IF NOT EXISTS rating
(
    user_id  BIGINT  NOT NULL REFERENCES users (id),
    book_id  BIGINT  NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    score    INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, book_id)
);

-- Table for shelves
CREATE TABLE IF NOT EXISTS shelf
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Table for user-book associations
CREATE TABLE IF NOT EXISTS user_book
(
    user_id  BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    book_id  BIGINT NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    shelf_id BIGINT NOT NULL REFERENCES shelf (id) ON DELETE CASCADE,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, book_id)
);

-- Table for user favourite genres
CREATE TABLE IF NOT EXISTS favourite_genre
(
    user_id  BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genre (id),
    PRIMARY KEY (user_id, genre_id)
);

CREATE TABLE IF NOT EXISTS recommendation
(
    user_id         BIGINT           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    book_id         BIGINT           NOT NULL REFERENCES book (id),
    predicted_score DOUBLE PRECISION NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, book_id)
);


-- Функція для обчислення середнього рейтингу і оновлення rating_count та average_rating
CREATE OR REPLACE FUNCTION update_book_rating() RETURNS TRIGGER AS
$$
DECLARE
    v_book_id BIGINT;
BEGIN
    IF TG_OP = 'DELETE' THEN
        v_book_id := OLD.book_id;
    ELSE
        v_book_id := NEW.book_id;
    END IF;

    UPDATE book
    SET average_rating = COALESCE((SELECT AVG(score) FROM rating WHERE book_id = v_book_id), 0),
        rating_count   = (SELECT COUNT(*) FROM rating WHERE book_id = v_book_id)
    WHERE id = v_book_id;

    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER update_book_rating_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON rating
    FOR EACH ROW
EXECUTE FUNCTION update_book_rating();



-- Функція для оновлення review_count для книги
CREATE OR REPLACE FUNCTION update_book_review_count() RETURNS TRIGGER AS
$$
DECLARE
    v_book_id BIGINT;
BEGIN
    IF TG_OP = 'DELETE' THEN
        v_book_id := OLD.book_id;
    ELSE
        v_book_id := NEW.book_id;
    END IF;

    UPDATE book
    SET review_count = (SELECT COUNT(*) FROM review WHERE book_id = v_book_id)
    WHERE id = v_book_id;

    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_book_review_count_trigger
    AFTER INSERT OR DELETE
    ON review
    FOR EACH ROW
EXECUTE FUNCTION update_book_review_count();


-- Функція для автоматичного додавання книги до бібліотеки користувача при додаванні рецензії
CREATE OR REPLACE FUNCTION add_book_to_library_on_review() RETURNS TRIGGER AS
$$
BEGIN
    IF NOT EXISTS (SELECT 1
                   FROM user_book
                   WHERE user_id = NEW.user_id
                     AND book_id = NEW.book_id) THEN
        INSERT INTO user_book (user_id, book_id, shelf_id, added_at)
        VALUES (NEW.user_id, NEW.book_id,
                (SELECT id FROM shelf WHERE name = 'READ'), NOW());
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER add_book_to_library_on_review_trigger
    AFTER INSERT
    ON review
    FOR EACH ROW
EXECUTE FUNCTION add_book_to_library_on_review();


-- Функція для автоматичного додавання книги до бібліотеки користувача при додаванні рейтингу
CREATE OR REPLACE FUNCTION add_book_to_library_on_rating() RETURNS TRIGGER AS
$$
BEGIN
    IF NOT EXISTS (SELECT 1
                   FROM user_book
                   WHERE user_id = NEW.user_id
                     AND book_id = NEW.book_id) THEN
        INSERT INTO user_book (user_id, book_id, shelf_id, added_at)
        VALUES (NEW.user_id, NEW.book_id,
                (SELECT id FROM shelf WHERE name = 'READ'), NOW());
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER add_book_to_library_on_rating_trigger
    AFTER INSERT
    ON rating
    FOR EACH ROW
EXECUTE FUNCTION add_book_to_library_on_rating();