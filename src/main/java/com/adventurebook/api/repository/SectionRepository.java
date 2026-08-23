package com.adventurebook.api.repository;

import com.adventurebook.api.model.Section;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByBookIdAndSectionNumber(Long bookId, Integer sectionNumber);
}
