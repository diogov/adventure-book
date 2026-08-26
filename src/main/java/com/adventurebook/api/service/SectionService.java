package com.adventurebook.api.service;

import com.adventurebook.api.dto.DecisionOptionRequest;
import com.adventurebook.api.dto.DecisionOptionResponse;
import com.adventurebook.api.dto.SaveOptionRequest;
import com.adventurebook.api.dto.SaveSectionRequest;
import com.adventurebook.api.dto.SectionResponse;
import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.ConsequenceType;
import com.adventurebook.api.model.Option;
import com.adventurebook.api.model.Section;
import com.adventurebook.api.model.SectionType;
import com.adventurebook.api.repository.BookRepository;
import com.adventurebook.api.repository.OptionRepository;
import com.adventurebook.api.repository.SectionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final OptionRepository optionRepository;
    private final BookRepository bookRepository;

    public SectionResponse getSection(Long bookId, Integer sectionNumber) {
        return SectionResponse.from(findSectionOrThrow(bookId, sectionNumber));
    }

    @Transactional
    public SectionResponse saveSection(Long bookId, Integer sectionNumber, SaveSectionRequest request) {
        List<SaveOptionRequest> optionRequests = request.options() == null ? List.of() : request.options();

        if (request.type() == SectionType.END && !optionRequests.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ending sections cannot have options");
        }
        if (request.type() == SectionType.BEGIN && sectionRepository.existsOtherBeginningSection(bookId, sectionNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Book " + bookId + " already has a beginning section");
        }

        Section section = sectionRepository
                .findByBookIdAndSectionNumber(bookId, sectionNumber)
                .orElseGet(() -> newSection(bookId, sectionNumber));

        section.setText(request.text());
        section.setType(request.type());
        section.getOptions().clear();
        for (SaveOptionRequest optionRequest : optionRequests) {
            Option option = new Option();
            option.setSection(section);
            option.setDescription(optionRequest.description());
            option.setNextSectionNumber(optionRequest.nextSectionNumber());
            option.setConsequenceType(optionRequest.consequenceType());
            option.setConsequenceValue(optionRequest.consequenceValue());
            option.setConsequenceText(optionRequest.consequenceText());
            section.getOptions().add(option);
        }

        return SectionResponse.from(sectionRepository.save(section));
    }

    private Section newSection(Long bookId, Integer sectionNumber) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + bookId + " not found"));
        Section section = new Section();
        section.setBook(book);
        section.setSectionNumber(sectionNumber);
        return section;
    }

    public DecisionOptionResponse decision(Long bookId, Integer sectionNumber, DecisionOptionRequest request) {
        Section currentSection = findSectionOrThrow(bookId, sectionNumber);
        Option option = findOptionOrThrow(request.optionId(), currentSection.getId());

        int newHealth = applyConsequence(request.currentHealth(), option);
        boolean dead = newHealth == 0;

        Section nextSection = findSectionOrThrow(bookId, option.getNextSectionNumber());
        return new DecisionOptionResponse(SectionResponse.from(nextSection), newHealth, dead, option.getConsequenceText());
    }

    private int applyConsequence(int currentHealth, Option option) {
        if (option.getConsequenceType() == null) {
            return currentHealth;
        }
        int delta = option.getConsequenceType() == ConsequenceType.GAIN_HEALTH
                ? option.getConsequenceValue()
                : -option.getConsequenceValue();
        return Math.max(currentHealth + delta, 0);
    }

    private Section findSectionOrThrow(Long bookId, Integer sectionNumber) {
        return sectionRepository
                .findByBookIdAndSectionNumber(bookId, sectionNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Section " + sectionNumber + " not found in book " + bookId));
    }

    private Option findOptionOrThrow(Long optionId, Long sectionId) {
        return optionRepository
                .findByIdAndSectionId(optionId, sectionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Option " + optionId + " not found in section"));
    }
}
