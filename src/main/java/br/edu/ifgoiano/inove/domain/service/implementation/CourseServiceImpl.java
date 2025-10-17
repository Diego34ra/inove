package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.course.CourseRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.repository.CourseRepository;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository cursoRepository;

    @Autowired
    private MyModelMapper mapper;

    @Autowired
    private InoveUtils inoveUtils;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public CourseResponseDTO create(CourseRequestDTO courseDTO) {
        Course course =  mapper.mapTo(courseDTO, Course.class);
        course.setCreationDate(LocalDateTime.now());
        return mapper.mapTo(cursoRepository.save(course), br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO.class);
    }

    @Override
    public List<CourseSimpleResponseDTO> findAll() {
        return mapper.toList(cursoRepository.findAll(), CourseSimpleResponseDTO.class);
    }

    @Override
    public CourseResponseDTO findOneById(Long courseId) {
        var course = cursoRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));
        return mapper.mapTo(course, br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO.class);
    }

    @Override
    public Course findById(Long courseId) {
        return cursoRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));
    }

    @Override
    public CourseResponseDTO update(Long courseId, CourseRequestDTO courseDTO) {
        Course courseModel = mapper.mapTo(courseDTO, Course.class);
        Course savedCourse = findById(courseId);
        savedCourse.setLastUpdateDate(LocalDateTime.now());
        BeanUtils.copyProperties(courseModel, savedCourse, inoveUtils.getNullPropertyNames(courseModel));
        return mapper.mapTo(cursoRepository.save(savedCourse), br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long courseId) {
        Course course = cursoRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));

        entityManager.createNativeQuery("DELETE FROM tb_feedback WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM tb_content WHERE section_id IN (SELECT id FROM tb_section WHERE course_id = ?1)")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM tb_student_course WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM tb_instructor_course WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM tb_admin_course WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM tb_user_completed_content WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM tb_section WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.flush();

        cursoRepository.deleteById(courseId);
    }

    @Override
    public Course saveUpdateDate(Long courseId) {
        Course course = findById(courseId);
        course.setLastUpdateDate(LocalDateTime.now());
        return cursoRepository.save(course);
    }

    @Override
    public void updateCourseImage(Long courseId, String imageUrl) {
        Course course = findById(courseId);
        course.setImageUrl(imageUrl);
        cursoRepository.save(course);
    }

    @Override
    public String getCourseImageUrl(Long courseId) {
        Course course = findById(courseId);
        return course.getImageUrl();
    }

    @Override
    public List<CourseSimpleResponseDTO> findCoursesByInstructor(Long instructorId) {
        List<Course> courses = cursoRepository.findByInstructorId(instructorId);
        return mapper.toList(courses, CourseSimpleResponseDTO.class);
    }

}
