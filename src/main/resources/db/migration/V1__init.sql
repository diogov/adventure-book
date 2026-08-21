CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    difficulty VARCHAR(20) NOT NULL
);

CREATE TABLE book_categories (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    CONSTRAINT uq_book_categories_book_category UNIQUE (book_id, category)
);

CREATE INDEX idx_book_categories_book_id ON book_categories (book_id);



INSERT INTO books (title, author, difficulty) VALUES ('The Crystal Caverns', 'Evelyn Stormrider', 'EASY');
INSERT INTO books (title, author, difficulty) VALUES ('Pirates of the Jade Sea', 'Marina Blackwood', 'HARD');
INSERT INTO books (title, author, difficulty) VALUES ('The Prisoner', 'Daniel El Fuego', 'HARD');

INSERT INTO book_categories (book_id, category) VALUES (1, 'ADVENTURE');
INSERT INTO book_categories (book_id, category) VALUES (1, 'HORROR');
INSERT INTO book_categories (book_id, category) VALUES (1, 'MYSTERY');
INSERT INTO book_categories (book_id, category) VALUES (2, 'ADVENTURE');
INSERT INTO book_categories (book_id, category) VALUES (2, 'FICTION');
INSERT INTO book_categories (book_id, category) VALUES (3, 'HORROR');
