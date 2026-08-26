package com.adventurebook.api.repository;

import com.adventurebook.api.model.Section;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByBookIdAndSectionNumber(Long bookId, Integer sectionNumber);

    List<Section> findByBookId(Long bookId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM sections WHERE book_id = :bookId AND type = 'BEGIN'"
                    + " AND section_number <> :sectionNumber)", nativeQuery = true)
    boolean existsOtherBeginningSection(@Param("bookId") Long bookId, @Param("sectionNumber") Integer sectionNumber);
}
