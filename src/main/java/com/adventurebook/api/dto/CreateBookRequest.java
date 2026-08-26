package com.adventurebook.api.dto;

import com.adventurebook.api.model.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record CreateBookRequest(
        @NotBlank String title, @NotBlank String author, @NotNull Difficulty difficulty, Set<String> categories) {}
