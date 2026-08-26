package com.adventurebook.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Category;
import com.adventurebook.api.model.Difficulty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import(BookRepository.class)
@TestPropertySource(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class BookRepositoryTest {

    @Autowired private BookRepository bookRepository;

    @Test
    void search_titleFilter_returnsMatchingBook() {
        seed("The Crystal Caverns", "Evelyn Stormrider", Difficulty.EASY, "ADVENTURE");
        seed("Pirates of the Jade Sea", "Marina Blackwood", Difficulty.MEDIUM, "ADVENTURE");

        Page<Book> result = bookRepository.search("crystal", null, null, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("The Crystal Caverns");
    }

    @Test
    void search_authorFilter_returnsMatchingBook() {
        seed("The Prisoner", "Daniel El Fuego", Difficulty.HARD, "HORROR");

        Page<Book> result = bookRepository.search(null, "el fuego", null, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAuthor()).isEqualTo("Daniel El Fuego");
    }

    @Test
    void search_categoryFilter_returnsMatchingBook() {
        seed("The Crystal Caverns", "Evelyn Stormrider", Difficulty.MEDIUM, "HORROR", "MYSTERY");
        seed("Pirates of the Jade Sea", "Marina Blackwood", Difficulty.EASY, "FICTION");

        Page<Book> result = bookRepository.search(null, null, "mystery", null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("The Crystal Caverns");
    }

    @Test
    void search_difficultyFilter_returnsMatchingBook() {
        seed("The Crystal Caverns", "Evelyn Stormrider", Difficulty.EASY);
        seed("Pirates of the Jade Sea", "Marina Blackwood", Difficulty.HARD);

        Page<Book> result = bookRepository.search(null, null, null, Difficulty.HARD, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Pirates of the Jade Sea");
    }

    @Test
    void search_noFilters_returnsAllBooks() {
        seed("The Crystal Caverns", "Evelyn Stormrider", Difficulty.EASY);
        seed("Pirates of the Jade Sea", "Marina Blackwood", Difficulty.HARD);
        seed("The Prisoner", "Daniel El Fuego", Difficulty.HARD).setValid(true);

        Page<Book> result = bookRepository.search(null, null, null, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void search_validFilter_returnsOnlyValidBooks() {
        seed("The Crystal Caverns", "Evelyn Stormrider", Difficulty.EASY).setValid(true);
        seed("Pirates of the Jade Sea", "Marina Blackwood", Difficulty.HARD);

        Page<Book> validOnly = bookRepository.search(null, null, null, null, true, PageRequest.of(0, 20));
        Page<Book> invalidOnly = bookRepository.search(null, null, null, null, false, PageRequest.of(0, 20));

        assertThat(validOnly.getContent()).extracting(Book::getTitle).containsExactly("The Crystal Caverns");
        assertThat(invalidOnly.getContent()).extracting(Book::getTitle).containsExactly("Pirates of the Jade Sea");
    }

    private Book seed(String title, String author, Difficulty difficulty, String... categories) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDifficulty(difficulty);
        for (String categoryName : categories) {
            Category category = new Category();
            category.setCategory(categoryName);
            category.setBook(book);
            book.getCategoryEntities().add(category);
        }
        return bookRepository.save(book);
    }
}
