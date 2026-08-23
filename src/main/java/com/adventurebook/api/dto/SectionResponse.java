package com.adventurebook.api.dto;

import com.adventurebook.api.model.Section;
import com.adventurebook.api.model.SectionType;
import java.util.List;

public record SectionResponse(Integer sectionNumber, String text, SectionType type, List<OptionResponse> options) {

    public static SectionResponse from(Section section) {
        return new SectionResponse(
                section.getSectionNumber(),
                section.getText(),
                section.getType(),
                section.getOptions().stream().map(OptionResponse::from).toList());
    }
}
