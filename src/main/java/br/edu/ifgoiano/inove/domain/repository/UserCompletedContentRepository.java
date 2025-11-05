package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.UserCompletedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCompletedContentRepository extends JpaRepository<UserCompletedContent,Long> {
    Long countByCourseIdAndUserId(Long courseId, Long userId);
    List<UserCompletedContent> findAllByCourseIdAndUserId(Long courseId, Long userId);

    @Modifying
    @Query("DELETE FROM UserCompletedContent ucc WHERE ucc.content.id = :contentId")
    void deleteByContentId(@Param("contentId") Long contentId);
}
