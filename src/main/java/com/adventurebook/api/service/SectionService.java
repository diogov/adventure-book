package com.adventurebook.api.service;

import com.adventurebook.api.dto.SectionResponse;
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

    public SectionResponse getSection(Long bookId, Integer sectionNumber) {
        return sectionRepository
                .findByBookIdAndSectionNumber(bookId, sectionNumber)
                .map(SectionResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Section " + sectionNumber + " not found in book " + bookId));
    }
}
