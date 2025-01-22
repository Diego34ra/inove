package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.course.CourseRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.repository.CourseRepository;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private MyModelMapper mapper;

    @Mock
    private InoveUtils inoveUtils;

    @Test
    void findAll_ShouldReturnAllCourses() {
        // Arrange
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Course 1");
        course1.setDescription("Description 1");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Course 2");
        course2.setDescription("Description 2");

        List<Course> courses = List.of(course1, course2);

        CourseSimpleResponseDTO dto1 = new CourseSimpleResponseDTO();
        dto1.setId(1L);
        dto1.setName("Course 1");

        CourseSimpleResponseDTO dto2 = new CourseSimpleResponseDTO();
        dto2.setId(2L);
        dto2.setName("Course 2");

        List<CourseSimpleResponseDTO> expectedDtos = List.of(dto1, dto2);

        when(courseRepository.findAll()).thenReturn(courses);
        when(mapper.toList(courses, CourseSimpleResponseDTO.class)).thenReturn(expectedDtos);

        // Act
        List<CourseSimpleResponseDTO> result = courseService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Course 1", result.get(0).getName());
        assertEquals("Course 2", result.get(1).getName());

        verify(courseRepository).findAll();
        verify(mapper).toList(courses, CourseSimpleResponseDTO.class);
    }

    @Test
    void findOneById_ShouldReturnCourse_WhenExists() {
        // Arrange
        Long courseId = 1L;

        Course course = new Course();
        course.setId(courseId);
        course.setName("Test Course");
        course.setDescription("Test Description");

        CourseResponseDTO expectedDto = new CourseResponseDTO();
        expectedDto.setId(courseId);
        expectedDto.setName("Test Course");
        expectedDto.setDescription("Test Description");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(mapper.mapTo(course, CourseResponseDTO.class)).thenReturn(expectedDto);

        // Act
        CourseResponseDTO result = courseService.findOneById(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        assertEquals("Test Course", result.getName());
        assertEquals("Test Description", result.getDescription());

        verify(courseRepository).findById(courseId);
    }

    @Test
    void create_ShouldCreateNewCourse() {
        // Arrange
        CourseRequestDTO requestDto = new CourseRequestDTO();
        requestDto.setName("New Course");
        requestDto.setDescription("New Description");

        Course course = new Course();
        course.setName("New Course");
        course.setDescription("New Description");

        Course savedCourse = new Course();
        savedCourse.setId(1L);
        savedCourse.setName("New Course");
        savedCourse.setDescription("New Description");

        CourseResponseDTO expectedDto = new CourseResponseDTO();
        expectedDto.setId(1L);
        expectedDto.setName("New Course");
        expectedDto.setDescription("New Description");

        when(mapper.mapTo(requestDto, Course.class)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);
        when(mapper.mapTo(savedCourse, CourseResponseDTO.class)).thenReturn(expectedDto);

        // Act
        CourseResponseDTO result = courseService.create(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Course", result.getName());
        assertEquals("New Description", result.getDescription());

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void update_ShouldUpdateExistingCourse() {
        // Arrange
        Long courseId = 1L;
        CourseRequestDTO requestDto = new CourseRequestDTO();
        requestDto.setName("Updated Course");
        requestDto.setDescription("Updated Description");

        Course existingCourse = new Course();
        existingCourse.setId(courseId);
        existingCourse.setName("Old Name");
        existingCourse.setDescription("Old Description");

        Course updatedCourse = new Course();
        updatedCourse.setId(courseId);
        updatedCourse.setName("Updated Course");
        updatedCourse.setDescription("Updated Description");

        CourseResponseDTO expectedDto = new CourseResponseDTO();
        expectedDto.setId(courseId);
        expectedDto.setName("Updated Course");
        expectedDto.setDescription("Updated Description");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(mapper.mapTo(requestDto, Course.class)).thenReturn(updatedCourse);
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);
        when(mapper.mapTo(updatedCourse, CourseResponseDTO.class)).thenReturn(expectedDto);

        // Act
        CourseResponseDTO result = courseService.update(courseId, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Course", result.getName());
        assertEquals("Updated Description", result.getDescription());

        verify(courseRepository).findById(courseId);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void delete_ShouldDeleteCourse_WhenExists() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);

        // Act
        courseService.delete(courseId);

        // Assert
        verify(courseRepository).deleteById(courseId);
    }

}
