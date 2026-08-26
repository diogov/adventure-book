package com.adventurebook.api.dto;

import com.adventurebook.api.model.ConsequenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveOptionRequest(
        @NotBlank String description,
        @NotNull Integer nextSectionNumber,
        ConsequenceType consequenceType,
        Integer consequenceValue,
        String consequenceText) {}
