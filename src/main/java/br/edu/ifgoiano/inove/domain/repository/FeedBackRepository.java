package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
    List<FeedBack> findByCourseId(Long courseId);
    Optional<FeedBack> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
