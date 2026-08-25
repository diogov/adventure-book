package com.adventurebook.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.adventurebook.api.dto.DecisionOptionRequest;
import com.adventurebook.api.dto.DecisionOptionResponse;
import com.adventurebook.api.dto.SectionResponse;
import com.adventurebook.api.model.ConsequenceType;
import com.adventurebook.api.model.Option;
import com.adventurebook.api.model.Section;
import com.adventurebook.api.model.SectionType;
import com.adventurebook.api.repository.OptionRepository;
import com.adventurebook.api.repository.SectionRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock private SectionRepository sectionRepository;
    @Mock private OptionRepository optionRepository;

    @InjectMocks private SectionService sectionService;

    @Test
    void getSection_sectionExists_returnsSectionResponse() {
        Section section = section(1L, 1, "You stand at the entrance.", SectionType.BEGIN);
        Option option = option(section, 100L, "Cross the rope bridge", 2, null, null);
        section.getOptions().add(option);
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 1)).thenReturn(Optional.of(section));

        SectionResponse response = sectionService.getSection(1L, 1);

        assertThat(response.sectionNumber()).isEqualTo(1);
        assertThat(response.text()).isEqualTo("You stand at the entrance.");
        assertThat(response.type()).isEqualTo(SectionType.BEGIN);
        assertThat(response.options()).hasSize(1);
        assertThat(response.options().get(0).description()).isEqualTo("Cross the rope bridge");
        assertThat(response.options().get(0).nextSectionNumber()).isEqualTo(2);
    }

    @Test
    void getSection_sectionDoesNotExist_throwsNotFound() {
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.getSection(1L, 99)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void decision_optionSurviving_returnsAliveWithReducedHealth() {
        Section current = section(1L, 1, "You stand at the entrance.", SectionType.BEGIN);
        Section next = section(2L, 2, "You cross the rope bridge.", SectionType.END);
        Option option = option(current, 100L, "Cross the rope bridge", 2, ConsequenceType.LOSE_HEALTH, 2);
        option.setConsequenceText("You scrape past, losing your footing for a moment.");
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 1)).thenReturn(Optional.of(current));
        when(optionRepository.findByIdAndSectionId(100L, 1L)).thenReturn(Optional.of(option));
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 2)).thenReturn(Optional.of(next));

        DecisionOptionResponse response = sectionService.decision(1L, 1, new DecisionOptionRequest(100L, 10));

        assertThat(response.health()).isEqualTo(8);
        assertThat(response.dead()).isFalse();
        assertThat(response.section().sectionNumber()).isEqualTo(2);
        assertThat(response.consequenceText()).isEqualTo("You scrape past, losing your footing for a moment.");
    }

    @Test
    void decision_optionToDie_returnsDeadWithZeroHealth() {
        Section current = section(1L, 1, "You stand at the entrance.", SectionType.BEGIN);
        Section next = section(3L, 3, "You search the rocky walls.", SectionType.END);
        Option option = option(current, 101L, "Search the rocky walls", 3, ConsequenceType.LOSE_HEALTH, 12);
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 1)).thenReturn(Optional.of(current));
        when(optionRepository.findByIdAndSectionId(101L, 1L)).thenReturn(Optional.of(option));
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 3)).thenReturn(Optional.of(next));

        DecisionOptionResponse response = sectionService.decision(1L, 1, new DecisionOptionRequest(101L, 10));

        assertThat(response.health()).isZero();
        assertThat(response.dead()).isTrue();
    }

    @Test
    void decision_optionWithoutConsequence_healthUnchanged() {
        Section current = section(1L, 1, "You stand at the entrance.", SectionType.BEGIN);
        Section next = section(2L, 2, "You cross the rope bridge.", SectionType.END);
        Option option = option(current, 100L, "Cross the rope bridge", 2, null, null);
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 1)).thenReturn(Optional.of(current));
        when(optionRepository.findByIdAndSectionId(100L, 1L)).thenReturn(Optional.of(option));
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 2)).thenReturn(Optional.of(next));

        DecisionOptionResponse response = sectionService.decision(1L, 1, new DecisionOptionRequest(100L, 10));

        assertThat(response.health()).isEqualTo(10);
        assertThat(response.consequenceText()).isNull();
    }

    @Test
    void decision_optionDoesNotExistInSection_throwsNotFound() {
        Section current = section(1L, 1, "You stand at the entrance.", SectionType.BEGIN);
        when(sectionRepository.findByBookIdAndSectionNumber(1L, 1)).thenReturn(Optional.of(current));

        assertThatThrownBy(() -> sectionService.decision(1L, 1, new DecisionOptionRequest(999L, 10)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Option 999 not found in section");
    }

    private Section section(Long id, int sectionNumber, String text, SectionType type) {
        Section section = new Section();
        section.setId(id);
        section.setSectionNumber(sectionNumber);
        section.setText(text);
        section.setType(type);
        return section;
    }

    private Option option(
            Section section,
            Long id,
            String description,
            int nextSectionNumber,
            ConsequenceType consequenceType,
            Integer consequenceValue) {
        Option option = new Option();
        option.setId(id);
        option.setSection(section);
        option.setDescription(description);
        option.setNextSectionNumber(nextSectionNumber);
        option.setConsequenceType(consequenceType);
        option.setConsequenceValue(consequenceValue);
        return option;
    }
}
