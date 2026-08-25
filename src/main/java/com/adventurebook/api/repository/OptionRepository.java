package com.adventurebook.api.repository;

import com.adventurebook.api.model.Option;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

    Optional<Option> findByIdAndSectionId(Long id, Long sectionId);
}
