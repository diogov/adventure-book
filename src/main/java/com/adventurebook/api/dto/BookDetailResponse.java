package com.adventurebook.api.dto;

import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import java.util.Set;

public record BookDetailResponse(
        Long id, String title, String author, Difficulty difficulty, Set<String> categories) {

    public static BookDetailResponse from(Book book) {
        return new BookDetailResponse(
                book.getId(), book.getTitle(), book.getAuthor(), book.getDifficulty(), book.getCategories());
    }
}
