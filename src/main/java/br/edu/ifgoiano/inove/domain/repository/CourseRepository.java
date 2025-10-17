package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select distinct c from Course c left join fetch c.instructors")
    List<Course> findAllWithInstructors();

    @Query("select c from Course c left join fetch c.instructors where c.id = :id")
    Optional<Course> findByIdWithInstructors(@Param("id") Long id);

    @Query("""
           select distinct c
           from Course c
           join c.instructors i
           left join fetch c.instructors
           where i.id = :instructorId
           """)
    List<Course> findByInstructorIdWithInstructors(@Param("instructorId") Long instructorId);
}
