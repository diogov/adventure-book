package com.adventurebook.api.service;

import com.adventurebook.api.dto.BookSearchResponse;
import com.adventurebook.api.model.Difficulty;
import com.adventurebook.api.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookSearchResponse> search(
            String title, String author, String category, Difficulty difficulty, Pageable pageable) {
        return bookRepository.search(title, author, category, difficulty, pageable).map(BookSearchResponse::from);
    }
}
