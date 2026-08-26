package com.adventurebook.api.controller;

import com.adventurebook.api.dto.AddCategoryRequest;
import com.adventurebook.api.dto.BookDetailResponse;
import com.adventurebook.api.dto.BookSearchResponse;
import com.adventurebook.api.dto.CreateBookRequest;
import com.adventurebook.api.dto.DecisionOptionRequest;
import com.adventurebook.api.dto.DecisionOptionResponse;
import com.adventurebook.api.dto.SaveSectionRequest;
import com.adventurebook.api.dto.SectionResponse;
import com.adventurebook.api.dto.ValidateBookResponse;
import com.adventurebook.api.model.Difficulty;
import com.adventurebook.api.service.BookService;
import com.adventurebook.api.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final SectionService sectionService;

    public BookController(BookService bookService, SectionService sectionService) {
        this.bookService = bookService;
        this.sectionService = sectionService;
    }

    @GetMapping
    public Page<BookSearchResponse> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) Boolean valid,
            @PageableDefault(size = 20) Pageable pageable) {
        return bookService.search(title, author, category, difficulty, valid, pageable);
    }

    @PostMapping
    public BookDetailResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @GetMapping("/{bookId}")
    public BookDetailResponse getById(@PathVariable Long bookId) {
        return bookService.getById(bookId);
    }

    @PostMapping("/{bookId}/validate")
    public ValidateBookResponse validate(@PathVariable Long bookId) {
        return bookService.validate(bookId);
    }

    @PostMapping("/{bookId}/categories")
    public BookDetailResponse addCategory(@PathVariable Long bookId, @Valid @RequestBody AddCategoryRequest request) {
        return bookService.addCategory(bookId, request.category());
    }

    @DeleteMapping("/{bookId}/categories/{category}")
    public BookDetailResponse removeCategory(@PathVariable Long bookId, @PathVariable String category) {
        return bookService.removeCategory(bookId, category);
    }

    @GetMapping("/{bookId}/sections/{sectionNumber}")
    public SectionResponse getSection(@PathVariable Long bookId, @PathVariable Integer sectionNumber) {
        return sectionService.getSection(bookId, sectionNumber);
    }

    @PutMapping("/{bookId}/sections/{sectionNumber}")
    public SectionResponse saveSection(
            @PathVariable Long bookId, @PathVariable Integer sectionNumber, @Valid @RequestBody SaveSectionRequest request) {
        return sectionService.saveSection(bookId, sectionNumber, request);
    }

    @GetMapping("/{bookId}/sections/{sectionNumber}/decision")
    public DecisionOptionResponse decision(
            @PathVariable Long bookId,
            @PathVariable Integer sectionNumber,
            @RequestParam Long optionId,
            @RequestParam Integer currentHealth) {
        return sectionService.decision(bookId, sectionNumber, new DecisionOptionRequest(optionId, currentHealth));
    }
}
