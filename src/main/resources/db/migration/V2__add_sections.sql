CREATE TABLE sections (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    section_number INTEGER NOT NULL,
    text TEXT NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('BEGIN', 'NODE', 'END')),
    CONSTRAINT uq_sections_book_number UNIQUE (book_id, section_number)
);

CREATE INDEX idx_sections_book_id ON sections (book_id);

CREATE TABLE options (
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT NOT NULL REFERENCES sections (id) ON DELETE CASCADE,
    description VARCHAR(1000) NOT NULL,
    next_section_number INTEGER NOT NULL
);

CREATE INDEX idx_options_section_id ON options (section_id);

INSERT INTO sections (book_id, section_number, text, type)
VALUES (1, 1, 'You stand at the entrance of the legendary Crystal Caverns. A cold breeze carries whispers from the darkness below.', 'BEGIN');
INSERT INTO sections (book_id, section_number, text, type)
VALUES (1, 2, 'You cross the rope bridge and find a chamber glittering with crystals.', 'END');
INSERT INTO sections (book_id, section_number, text, type)
VALUES (1, 3, 'You search the rocky walls and find a hidden passage leading back outside.', 'END');

INSERT INTO options (section_id, description, next_section_number) VALUES (1, 'Cross the rope bridge', 2);
INSERT INTO options (section_id, description, next_section_number) VALUES (1, 'Search the rocky walls', 3);
