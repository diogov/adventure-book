package com.adventurebook.api.dto;

import com.adventurebook.api.model.SectionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SaveSectionRequest(
        @NotBlank String text, @NotNull SectionType type, @Valid List<SaveOptionRequest> options) {}
