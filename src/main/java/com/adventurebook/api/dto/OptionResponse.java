package com.adventurebook.api.dto;

import com.adventurebook.api.model.Option;

public record OptionResponse(String description, Integer nextSectionNumber) {

    public static OptionResponse from(Option option) {
        return new OptionResponse(option.getDescription(), option.getNextSectionNumber());
    }
}
