package com.adventurebook.api.dto;

import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import java.util.Set;

public record BookDetailResponse(
        Long id,
        String title,
        String author,
        Difficulty difficulty,
        Set<String> categories,
        Integer beginningSectionNumber,
        boolean valid) {

    public static BookDetailResponse from(Book book, Integer beginningSectionNumber) {
        return new BookDetailResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDifficulty(),
                book.getCategories(),
                beginningSectionNumber,
                book.isValid());
    }
}
