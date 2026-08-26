package com.adventurebook.api.service;

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
import java.util.Set;
import java.util.stream.Collectors;
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
    private final SectionRepository sectionRepository;

    @Transactional(readOnly = true)
    public Page<BookSearchResponse> search(
            String title, String author, String category, Difficulty difficulty, Boolean valid, Pageable pageable) {
        return bookRepository.search(title, author, category, difficulty, valid, pageable).map(BookSearchResponse::from);
    }

    public BookDetailResponse createBook(CreateBookRequest request) {
        Book book = new Book();
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setDifficulty(request.difficulty());
        if (request.categories() != null) {
            for (String category : request.categories()) {
                book.addCategory(category.toUpperCase());
            }
        }
        bookRepository.save(book);
        return BookDetailResponse.from(book, null);
    }


    @Transactional(readOnly = true)
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


    public ValidateBookResponse validate(Long bookId) {
        Book book = findBookOrThrow(bookId);
        List<Section> sections = sectionRepository.findByBookId(bookId);
        boolean valid = isValidBook(sections);
        book.setValid(valid);
        return new ValidateBookResponse(valid);
    }

    private boolean isValidBook(List<Section> sections) {
        long beginningCount =
                sections.stream().filter(section -> section.getType() == SectionType.BEGIN).count();
        if (beginningCount != 1) {
            return false;
        }
        boolean hasEnding = sections.stream().anyMatch(section -> section.getType() == SectionType.END);
        if (!hasEnding) {
            return false;
        }

        Set<Integer> sectionNumbers = sections.stream().map(Section::getSectionNumber).collect(Collectors.toSet());
        for (Section section : sections) {
            if (section.getType() != SectionType.END && section.getOptions().isEmpty()) {
                return false;
            }
            for (Option option : section.getOptions()) {
                if (!sectionNumbers.contains(option.getNextSectionNumber())) {
                    return false;
                }
            }
        }
        return true;
    }
}
