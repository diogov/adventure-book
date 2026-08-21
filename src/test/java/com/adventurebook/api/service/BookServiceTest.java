package com.adventurebook.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adventurebook.api.dto.BookSearchResponse;
import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import com.adventurebook.api.repository.BookRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;

    @Test
    void search_repositoryReturnsBooks_mapsToSearchResponses() {
        BookService bookService = new BookService(bookRepository);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Crystal Caverns");
        book.setAuthor("Evelyn Stormrider");
        book.setDifficulty(Difficulty.EASY);
        Pageable pageable = PageRequest.of(0, 20);

        when(bookRepository.search("crystal", null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 1));

        Page<BookSearchResponse> result = bookService.search("crystal", null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        BookSearchResponse response = result.getContent().get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("The Crystal Caverns");
        assertThat(response.author()).isEqualTo("Evelyn Stormrider");
        assertThat(response.difficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void search_withFilters_passesFiltersToRepository() {
        BookService bookService = new BookService(bookRepository);
        Pageable pageable = PageRequest.of(0, 20);

        when(bookRepository.search("title", "author", "category", Difficulty.HARD, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        bookService.search("title", "author", "category", Difficulty.HARD, pageable);

        verify(bookRepository).search("title", "author", "category", Difficulty.HARD, pageable);
    }
}
