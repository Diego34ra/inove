package br.edu.ifgoiano.inove.domain.service.implementation;
import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.course.CourseRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.model.Section;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.repository.CourseRepository;
import br.edu.ifgoiano.inove.domain.repository.UserRepository;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired private CourseRepository cursoRepository;
    @Autowired private MyModelMapper mapper;
    @Autowired private InoveUtils inoveUtils;
    @Autowired private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public CourseResponseDTO create(CourseRequestDTO courseDTO) {
        var course = new Course();
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setCreationDate(LocalDateTime.now());

        if (courseDTO.getInstructors() != null && !courseDTO.getInstructors().isEmpty()) {
            var instIds = courseDTO.getInstructors().stream()
                    .map(User::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            var instructors = new HashSet<User>(userRepository.findAllById(instIds));
            instructors.forEach(u -> u.getInstructor_courses().add(course));
            course.setInstructors(instructors);
        } else {
            course.setInstructors(new HashSet<>());
        }

        if (courseDTO.getAdmins() != null && !courseDTO.getAdmins().isEmpty()) {
            var adminIds = courseDTO.getAdmins().stream()
                    .map(User::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            var admins = new HashSet<User>(userRepository.findAllById(adminIds));
            admins.forEach(u -> u.getAdmin_courses().add(course));
            course.setAdmins(admins);
        } else {
            course.setAdmins(new HashSet<>());
        }

        if (courseDTO.getSections() != null && !courseDTO.getSections().isEmpty()) {
            var sections = courseDTO.getSections().stream().map(sDto -> {
                var s = new Section();
                s.setTitle(sDto.getTitle());
                s.setDescription(sDto.getDescription());
                s.setCourse(course);
                return s;
            }).toList();
            course.setSections(sections);
        } else {
            course.setSections(List.of());
        }

        var saved = cursoRepository.save(course);
        return mapper.mapTo(saved, CourseResponseDTO.class);
    }

    @Override @Transactional(readOnly = true)
    public List<CourseSimpleResponseDTO> findAll() {
        var courses = cursoRepository.findAllWithInstructors();
        return mapper.toList(courses, CourseSimpleResponseDTO.class);
    }

    @Override @Transactional(readOnly = true)
    public CourseResponseDTO findOneById(Long courseId) {
        var course = cursoRepository.findByIdWithInstructors(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));
        return mapper.mapTo(course, CourseResponseDTO.class);
    }

    @Override @Transactional(readOnly = true)
    public Course findById(Long courseId) {
        return cursoRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));
    }

    @Override
    @Transactional
    public CourseResponseDTO update(Long courseId, CourseRequestDTO dto) {
        Course saved = cursoRepository.findByIdWithInstructorsAndAdmins(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));

        if (dto.getName() != null)        saved.setName(dto.getName());
        if (dto.getDescription() != null) saved.setDescription(dto.getDescription());
        saved.setLastUpdateDate(LocalDateTime.now());

        if (dto.getInstructors() != null) {
            Set<Long> newIds = dto.getInstructors().stream()
                    .map(User::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<User> newSet = new HashSet<>(userRepository.findAllById(newIds));

            Set<User> toRemove = new HashSet<>(saved.getInstructors());
            toRemove.removeAll(newSet);
            toRemove.forEach(u -> u.getInstructor_courses().remove(saved));

            Set<User> toAdd = new HashSet<>(newSet);
            toAdd.removeAll(saved.getInstructors());
            toAdd.forEach(u -> u.getInstructor_courses().add(saved));

            saved.setInstructors(newSet);
        }

        if (dto.getAdmins() != null) {
            Set<Long> newIds = dto.getAdmins().stream()
                    .map(User::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<User> newSet = new HashSet<>(userRepository.findAllById(newIds));

            Set<User> toRemove = new HashSet<>(saved.getAdmins());
            toRemove.removeAll(newSet);
            toRemove.forEach(u -> u.getAdmin_courses().remove(saved));

            Set<User> toAdd = new HashSet<>(newSet);
            toAdd.removeAll(saved.getAdmins());
            toAdd.forEach(u -> u.getAdmin_courses().add(saved));

            saved.setAdmins(newSet);
        }

        if (dto.getSections() != null) {
            dto.getSections().forEach(s -> s.setCourse(saved));
            saved.setSections(dto.getSections());
        }

        Course persisted = cursoRepository.save(saved);
        return mapper.mapTo(persisted, CourseResponseDTO.class);
    }

    @Override
    @Transactional
    public void delete(Long courseId) {
        Course course = cursoRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado nenhum curso com esse id."));

        entityManager.createNativeQuery("DELETE FROM tb_user_completed_content WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery("DELETE FROM tb_feedback WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM tb_content WHERE section_id IN (SELECT id FROM tb_section WHERE course_id = ?1)")
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

        entityManager.createNativeQuery("DELETE FROM tb_section WHERE course_id = ?1")
                .setParameter(1, courseId)
                .executeUpdate();

        entityManager.flush();

        cursoRepository.deleteById(courseId);
    }

    // ===== demais métodos =====
    @Override @Transactional
    public Course saveUpdateDate(Long courseId) {
        Course course = findById(courseId);
        course.setLastUpdateDate(LocalDateTime.now());
        return cursoRepository.save(course);
    }

    @Override @Transactional
    public void updateCourseImage(Long courseId, String imageUrl) {
        Course course = findById(courseId);
        course.setImageUrl(imageUrl);
        cursoRepository.save(course);
    }

    @Override @Transactional(readOnly = true)
    public String getCourseImageUrl(Long courseId) {
        return findById(courseId).getImageUrl();
    }

    @Override @Transactional(readOnly = true)
    public List<CourseSimpleResponseDTO> findCoursesByInstructor(Long instructorId) {
        var courses = cursoRepository.findByInstructorIdWithInstructors(instructorId);
        return mapper.toList(courses, CourseSimpleResponseDTO.class);
    }
}
