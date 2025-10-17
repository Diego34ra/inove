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
    Optional<Content> findByIdAndSectionId(Long contentId, Long sectionId);

    @Modifying
    @Query("DELETE FROM Content c WHERE c.id = :contentId AND c.section.id = :sectionId")
    void deleteByIdAndSectionId(@Param("contentId") Long contentId, @Param("sectionId") Long sectionId);
}
