package com.adventurebook.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import com.adventurebook.api.model.Option;
import com.adventurebook.api.model.Section;
import com.adventurebook.api.model.SectionType;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import(BookRepository.class)
@TestPropertySource(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class SectionRepositoryTest {

    @Autowired private BookRepository bookRepository;
    @Autowired private SectionRepository sectionRepository;

    @Test
    void findByBookIdAndSectionNumber_sectionExists_returnsSectionWithOptions() {
        Book book = new Book();
        book.setTitle("The Crystal Caverns");
        book.setAuthor("Evelyn Stormrider");
        book.setDifficulty(Difficulty.EASY);
        bookRepository.save(book);

        Section section = new Section();
        section.setBook(book);
        section.setSectionNumber(1);
        section.setText("You stand at the entrance.");
        section.setType(SectionType.BEGIN);

        Option option = new Option();
        option.setSection(section);
        option.setDescription("Cross the rope bridge");
        option.setNextSectionNumber(2);
        section.getOptions().add(option);

        sectionRepository.save(section);

        Optional<Section> result = sectionRepository.findByBookIdAndSectionNumber(book.getId(), 1);

        assertThat(result).isPresent();
        assertThat(result.get().getText()).isEqualTo("You stand at the entrance.");
        assertThat(result.get().getOptions()).hasSize(1);
        assertThat(result.get().getOptions().get(0).getDescription()).isEqualTo("Cross the rope bridge");
    }

    @Test
    void findByBookIdAndSectionNumber_sectionDoesNotExist_returnsEmpty() {
        Optional<Section> result = sectionRepository.findByBookIdAndSectionNumber(1L, 99);

        assertThat(result).isEmpty();
    }
}
