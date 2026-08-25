package com.adventurebook.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.ConsequenceType;
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
class OptionRepositoryTest {

    @Autowired private BookRepository bookRepository;
    @Autowired private SectionRepository sectionRepository;
    @Autowired private OptionRepository optionRepository;

    @Test
    void findByIdAndSectionId_optionBelongsToSection_returnsOption() {
        Section section = seedSectionWithOption();
        Long optionId = section.getOptions().get(0).getId();

        Optional<Option> result = optionRepository.findByIdAndSectionId(optionId, section.getId());

        assertThat(result).isPresent();
    }

    @Test
    void findByIdAndSectionId_optionBelongsToDifferentSection_returnsEmpty() {
        Section section = seedSectionWithOption();
        Long optionId = section.getOptions().get(0).getId();

        Optional<Option> result = optionRepository.findByIdAndSectionId(optionId, 1000L);

        assertThat(result).isEmpty();
    }

    private Section seedSectionWithOption() {
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
        option.setConsequenceType(ConsequenceType.LOSE_HEALTH);
        option.setConsequenceValue(2);
        section.getOptions().add(option);

        return sectionRepository.save(section);
    }
}
