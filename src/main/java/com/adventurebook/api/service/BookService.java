package com.adventurebook.api.service;

import com.adventurebook.api.dto.BookDetailResponse;
import com.adventurebook.api.dto.BookSearchResponse;
import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import com.adventurebook.api.repository.BookRepository;
import com.adventurebook.api.repository.BookWithBeginningSection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookSearchResponse> search(
            String title, String author, String category, Difficulty difficulty, Pageable pageable) {
        return bookRepository.search(title, author, category, difficulty, pageable).map(BookSearchResponse::from);
    }

    public BookDetailResponse getById(Long bookId) {
        BookWithBeginningSection result = bookRepository
                .findByIdWithBeginningSection(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + bookId + " not found"));
        return BookDetailResponse.from(result.book(), result.beginningSectionNumber());
    }

    public BookDetailResponse addCategory(Long bookId, String category) {
        Book book = findBookOrThrow(bookId);
        book.addCategory(category.toUpperCase());
        return BookDetailResponse.from(book, null);
    }

    public BookDetailResponse removeCategory(Long bookId, String category) {
        Book book = findBookOrThrow(bookId);
        book.removeCategory(category.toUpperCase());
        return BookDetailResponse.from(book, null);
    }

    private Book findBookOrThrow(Long bookId) {
        return bookRepository
                .findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book " + bookId + " not found"));
    }
}
