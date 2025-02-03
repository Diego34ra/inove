package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.request.course.CourseRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.domain.model.Course;

import java.util.List;

public interface CourseService {

    CourseResponseDTO create(CourseRequestDTO courseDTO);

    List<CourseSimpleResponseDTO> findAll();

    Course findById(Long courseId);

    CourseResponseDTO findOneById(Long courseId);

    CourseResponseDTO update(Long courseId, CourseRequestDTO courseDTO);

    void delete(Long courseId);

    Course saveUpdateDate(Long courseId);

    void updateCourseImage(Long courseId, String imageUrl);

    String getCourseImageUrl(Long courseId);

    List<CourseSimpleResponseDTO> findCoursesByInstructor(Long instructorId);
}
