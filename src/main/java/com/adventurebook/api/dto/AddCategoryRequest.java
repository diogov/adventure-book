package com.adventurebook.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AddCategoryRequest(@NotBlank String category) {}
