package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.UserCompletedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCompletedContentRepository extends JpaRepository<UserCompletedContent,Long> {
    Long countByCourseIdAndUserId(Long courseId, Long userId);
    List<UserCompletedContent> findAllByCourseIdAndUserId(Long courseId, Long userId);
}
