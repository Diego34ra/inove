package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content,Long> {
    List<Content> findBySectionId(Long sectionId);
    List<Content> findBySectionIdOrderByIdAsc(Long sectionId);
    Optional<Content> findByIdAndSectionId(Long contentId, Long sectionId);
    Optional<Content> findByIdAndSectionIdAndSection_Course_Id(Long contentId, Long sectionId, Long courseId);

    @Modifying
    @Query("DELETE FROM Content c WHERE c.id = :contentId AND c.section.id = :sectionId")
    void deleteByIdAndSectionId(@Param("contentId") Long contentId, @Param("sectionId") Long sectionId);

    Long countBySection_Course_Id(Long courseId);
}
