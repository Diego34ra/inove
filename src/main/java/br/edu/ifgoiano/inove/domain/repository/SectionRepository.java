package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section,Long> {
    List<Section> findByCourseIdOrderByIdAsc(Long courseId);
    Optional<Section> findByIdAndCourseId(Long sectionId, Long courseId);
}
