package com.adventurebook.api.dto;

import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import java.util.Set;

public record BookSearchResponse(
        Long id, String title, String author, Difficulty difficulty, Set<String> categories) {

    public static BookSearchResponse from(Book book) {
        return new BookSearchResponse(
                book.getId(), book.getTitle(), book.getAuthor(), book.getDifficulty(), book.getCategories());
    }
}
