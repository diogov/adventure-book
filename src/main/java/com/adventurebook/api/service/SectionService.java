package com.adventurebook.api.service;

import com.adventurebook.api.dto.DecisionOptionRequest;
import com.adventurebook.api.dto.DecisionOptionResponse;
import com.adventurebook.api.dto.SectionResponse;
import com.adventurebook.api.model.ConsequenceType;
import com.adventurebook.api.model.Option;
import com.adventurebook.api.model.Section;
import com.adventurebook.api.repository.OptionRepository;
import com.adventurebook.api.repository.SectionRepository;
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

    public SectionResponse getSection(Long bookId, Integer sectionNumber) {
        return SectionResponse.from(findSectionOrThrow(bookId, sectionNumber));
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
