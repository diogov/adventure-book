package com.adventurebook.api.repository;

import com.adventurebook.api.model.Book;
import com.adventurebook.api.model.Difficulty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class BookRepository extends SimpleJpaRepository<Book, Long> {

    private final EntityManager entityManager;

    public BookRepository(EntityManager entityManager) {
        super(Book.class, entityManager);
        this.entityManager = entityManager;
    }

    public Page<Book> search(String title, String author, String category, Difficulty difficulty, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("FROM Book b LEFT JOIN b.categoryEntities c WHERE 1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (StringUtils.hasText(title)) {
            jpql.append(" AND LOWER(b.title) LIKE :title");
            params.put("title", "%" + title.toLowerCase() + "%");
        }
        if (StringUtils.hasText(author)) {
            jpql.append(" AND LOWER(b.author) LIKE :author");
            params.put("author", "%" + author.toLowerCase() + "%");
        }
        if (StringUtils.hasText(category)) {
            jpql.append(" AND UPPER(c.category) = :category");
            params.put("category", category.toUpperCase());
        }
        if (difficulty != null) {
            jpql.append(" AND b.difficulty = :difficulty");
            params.put("difficulty", difficulty);
        }

        String baseJpql = jpql.toString();

        TypedQuery<Book> query = entityManager.createQuery("SELECT DISTINCT b " + baseJpql, Book.class);
        params.forEach(query::setParameter);
        query.setFirstResult(Math.toIntExact(pageable.getOffset()));
        query.setMaxResults(pageable.getPageSize());
        List<Book> results = query.getResultList();

        TypedQuery<Long> countQuery =
                entityManager.createQuery("SELECT COUNT(DISTINCT b) " + baseJpql, Long.class);
        params.forEach(countQuery::setParameter);

        return PageableExecutionUtils.getPage(results, pageable, countQuery::getSingleResult);
    }
}
