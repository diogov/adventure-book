package com.adventurebook.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adventurebook.api.dto.BookDetailResponse;
import com.adventurebook.api.dto.BookSearchResponse;
import com.adventurebook.api.dto.CreateBookRequest;
import com.adventurebook.api.dto.ValidateBookResponse;
import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import com.adventurebook.api.model.Option;
import com.adventurebook.api.model.Section;
import com.adventurebook.api.model.SectionType;
import com.adventurebook.api.repository.BookRepository;
import com.adventurebook.api.repository.BookWithBeginningSection;
import com.adventurebook.api.repository.SectionRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private SectionRepository sectionRepository;

    @InjectMocks private BookService bookService;

    @Test
    void search_repositoryReturnsBooks_mapsToSearchResponses() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Crystal Caverns");
        book.setAuthor("Evelyn Stormrider");
        book.setDifficulty(Difficulty.EASY);
        Pageable pageable = PageRequest.of(0, 20);

        when(bookRepository.search("crystal", null, null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 1));

        Page<BookSearchResponse> result = bookService.search("crystal", null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        BookSearchResponse response = result.getContent().get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("The Crystal Caverns");
        assertThat(response.author()).isEqualTo("Evelyn Stormrider");
        assertThat(response.difficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void search_withFilters_passesFiltersToRepository() {
        Pageable pageable = PageRequest.of(0, 20);

        when(bookRepository.search("title", "author", "category", Difficulty.HARD, true, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        bookService.search("title", "author", "category", Difficulty.HARD, true, pageable);

        verify(bookRepository).search("title", "author", "category", Difficulty.HARD, true, pageable);
    }

    @Test
    void getById_bookExists_returnsDetailResponse() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Crystal Caverns");
        book.setAuthor("Evelyn Stomrrider");
        book.setDifficulty(Difficulty.EASY);
        when(bookRepository.findByIdWithBeginningSection(1L))
                .thenReturn(Optional.of(new BookWithBeginningSection(book, 1)));

        BookDetailResponse response = bookService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("The Crystal Caverns");
        assertThat(response.beginningSectionNumber()).isEqualTo(1);
    }

    @Test
    void getById_bookDoesNotExist_throwsNotFound() {
        when(bookRepository.findByIdWithBeginningSection(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(1L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void addCategory_bookExists_addsCategoryAndReturnsDetailResponse() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDetailResponse response = bookService.addCategory(1L, "horror");

        assertThat(book.getCategories()).containsExactly("HORROR");
        assertThat(response.categories()).containsExactly("HORROR");
    }

    @Test
    void addCategory_bookDoesNotExist_throwsNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.addCategory(1L, "horror")).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void removeCategory_bookExists_removesCategoryAndReturnsDetailResponse() {
        Book book = new Book();
        book.setId(1L);
        book.addCategory("HORROR");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDetailResponse response = bookService.removeCategory(1L, "horror");

        assertThat(book.getCategories()).isEmpty();
        assertThat(response.categories()).isEmpty();
    }

    @Test
    void removeCategory_bookDoesNotExist_throwsNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.removeCategory(1L, "horror")).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void createBook_savesBookWithUppercasedCategories() {
        BookDetailResponse response =
                bookService.createBook(new CreateBookRequest("The Crystal Caverns", "Evelyn Stormrider", Difficulty.EASY, Set.of("horror")));

        assertThat(response.title()).isEqualTo("The Crystal Caverns");
        assertThat(response.categories()).containsExactly("HORROR");
        assertThat(response.valid()).isFalse();
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void validate_bookHasSingleBeginningAndEndingAndValidLinks_marksValidTrue() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Section beginning = section(1, "Start", SectionType.BEGIN);
        Section ending = section(2, "The End", SectionType.END);
        beginning.getOptions().add(option(beginning, "Go on", 2));
        when(sectionRepository.findByBookId(1L)).thenReturn(List.of(beginning, ending));

        ValidateBookResponse response = bookService.validate(1L);

        assertThat(response.valid()).isTrue();
        assertThat(book.isValid()).isTrue();
    }

    @Test
    void validate_bookHasIntermediateNodeSection_marksValidTrue() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Section beginning = section(1, "Start", SectionType.BEGIN);
        Section node = section(2, "Middle", SectionType.NODE);
        Section ending = section(3, "The End", SectionType.END);
        beginning.getOptions().add(option(beginning, "Go to the middle", 2));
        node.getOptions().add(option(node, "Go to the end", 3));
        when(sectionRepository.findByBookId(1L)).thenReturn(List.of(beginning, node, ending));

        ValidateBookResponse response = bookService.validate(1L);

        assertThat(response.valid()).isTrue();
    }

    @Test
    void validate_bookHasNoBeginning_marksValidFalse() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Section ending = section(1, "The End", SectionType.END);
        when(sectionRepository.findByBookId(1L)).thenReturn(List.of(ending));

        ValidateBookResponse response = bookService.validate(1L);

        assertThat(response.valid()).isFalse();
    }

    @Test
    void validate_bookHasNoEnding_marksValidFalse() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Section beginning = section(1, "Start", SectionType.BEGIN);
        beginning.getOptions().add(option(beginning, "Go on", 1));
        when(sectionRepository.findByBookId(1L)).thenReturn(List.of(beginning));

        ValidateBookResponse response = bookService.validate(1L);

        assertThat(response.valid()).isFalse();
    }

    @Test
    void validate_optionPointsToNonExistentSection_marksValidFalse() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Section beginning = section(1, "Start", SectionType.BEGIN);
        Section ending = section(2, "The End", SectionType.END);
        beginning.getOptions().add(option(beginning, "Go on", 99));
        when(sectionRepository.findByBookId(1L)).thenReturn(List.of(beginning, ending));

        ValidateBookResponse response = bookService.validate(1L);

        assertThat(response.valid()).isFalse();
    }

    @Test
    void validate_nonEndingSectionHasNoOptions_marksValidFalse() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Section beginning = section(1, "Start", SectionType.BEGIN);
        Section ending = section(2, "The End", SectionType.END);
        when(sectionRepository.findByBookId(1L)).thenReturn(List.of(beginning, ending));

        ValidateBookResponse response = bookService.validate(1L);

        assertThat(response.valid()).isFalse();
    }

    private Section section(int sectionNumber, String text, SectionType type) {
        Section section = new Section();
        section.setSectionNumber(sectionNumber);
        section.setText(text);
        section.setType(type);
        return section;
    }

    private Option option(Section section, String description, int nextSectionNumber) {
        Option option = new Option();
        option.setSection(section);
        option.setDescription(description);
        option.setNextSectionNumber(nextSectionNumber);
        return option;
    }
}
