package com.adventurebook.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.adventurebook.api.dto.SectionResponse;
import com.adventurebook.api.model.Option;
import com.adventurebook.api.model.Section;
import com.adventurebook.api.model.SectionType;
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

    @InjectMocks private SectionService sectionService;

    @Test
    void getSection_sectionExists_returnsSectionResponse() {
        Section section = new Section();
        section.setSectionNumber(1);
        section.setText("You stand at the entrance.");
        section.setType(SectionType.BEGIN);

        Option option = new Option();
        option.setDescription("Cross the rope bridge");
        option.setNextSectionNumber(2);
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
}
