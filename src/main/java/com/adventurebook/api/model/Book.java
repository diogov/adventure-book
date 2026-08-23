package com.adventurebook.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<Category> categoryEntities = new ArrayList<>();

    public Set<String> getCategories() {
        return categoryEntities.stream().map(Category::getCategory).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void addCategory(String name) {
        if (categoryEntities.stream().noneMatch(c -> c.getCategory().equals(name))) {
            Category category = new Category();
            category.setCategory(name);
            category.setBook(this);
            categoryEntities.add(category);
        }
    }

    public void removeCategory(String name) {
        categoryEntities.removeIf(c -> c.getCategory().equals(name));
    }
}
