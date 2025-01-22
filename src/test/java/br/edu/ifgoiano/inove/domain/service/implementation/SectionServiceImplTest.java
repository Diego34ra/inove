package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.section.SectionRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.model.Section;
import br.edu.ifgoiano.inove.domain.repository.SectionRepository;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectionServiceImplTest {
    @InjectMocks
    private SectionServiceImpl sectionService;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private InoveUtils inoveUtils;

    @Mock
    private MyModelMapper mapper;

    @Test
    void list_ShouldReturnAllSectionsForCourse(){
        Long courseId  = 1L;
        Course course = new Course();
        course.setId(courseId);

        Section section1 = new Section();
        section1.setId(1L);
        section1.setTitle("Section 1 Title");
        section1.setDescription("Section 1 description");
        section1.setCourse(course);

        Section section2 = new Section();
        section2.setId(2L);
        section2.setTitle("Section 2 Title");
        section2.setDescription("Section 2 description");
        section2.setCourse(course);

        List<Section> sections = List.of(section1, section2);

        SectionSimpleResponseDTO dto1 = new SectionSimpleResponseDTO();
        dto1.setId(1L);
        dto1.setTitle("Section 1 Title");
        dto1.setDescription("Section 1 description");

        SectionSimpleResponseDTO dto2 = new SectionSimpleResponseDTO();
        dto2.setId(2L);
        dto2.setTitle("Section 2 Title");
        dto2.setDescription("Section 2 description");

        List<SectionSimpleResponseDTO> expetedDtos = List.of(dto1, dto2);

        when(sectionRepository.findByCourseId(courseId)).thenReturn(sections);
        when(mapper.toList(sections,SectionSimpleResponseDTO.class)).thenReturn(expetedDtos);

        List<SectionSimpleResponseDTO> result = sectionService.list(courseId);

        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals("Section 1 Title", result.get(0).getTitle());
        assertEquals("Section 1 description", result.get(0).getDescription());
        assertEquals("Section 2 Title", result.get(1).getTitle());
        assertEquals("Section 2 description", result.get(1).getDescription());

        verify(sectionRepository).findByCourseId(courseId);
        verify(mapper).toList(sections, SectionSimpleResponseDTO.class);
    }

    @Test
    void findOne_ShouldReturnSection_WhenExists() {
        Long courseId = 1L;
        Long sectionId = 1L;

        Course course = new Course();
        course.setId(courseId);

        Section section = new Section();
        section.setId(sectionId);
        section.setTitle("Test Section");
        section.setDescription("Test Description");
        section.setCourse(course);
        section.setContents(new ArrayList<>());

        SectionResponseDTO expectedDto = new SectionResponseDTO();
        expectedDto.setId(sectionId);
        expectedDto.setTitle("Test Section");
        expectedDto.setDescription("Test Description");

        when(sectionRepository.findByIdAndCourseId(sectionId, courseId))
                .thenReturn(Optional.of(section));
        when(mapper.mapTo(section, SectionResponseDTO.class))
                .thenReturn(expectedDto);

        SectionResponseDTO result = sectionService.findOne(courseId, sectionId);

        assertNotNull(result);
        assertEquals(sectionId, result.getId());
        assertEquals("Test Section", result.getTitle());
        assertEquals("Test Description", result.getDescription());

        verify(sectionRepository).findByIdAndCourseId(sectionId, courseId);
    }

    @Test
    void create_ShouldCreateNewSection() {
        Long courseId = 1L;
        SectionRequestDTO requestDto = new SectionRequestDTO();
        requestDto.setTitle("New Section");
        requestDto.setDescription("New Description");

        Course course = new Course();
        course.setId(courseId);

        Section section = new Section();
        section.setTitle("New Section");
        section.setDescription("New Description");
        section.setCourse(course);
        section.setContents(new ArrayList<>());

        Section savedSection = new Section();
        savedSection.setId(1L);
        savedSection.setTitle("New Section");
        savedSection.setDescription("New Description");
        savedSection.setCourse(course);
        savedSection.setContents(new ArrayList<>());

        SectionResponseDTO expectedDto = new SectionResponseDTO();
        expectedDto.setId(1L);
        expectedDto.setTitle("New Section");
        expectedDto.setDescription("New Description");

        when(courseService.findById(courseId)).thenReturn(course);
        when(mapper.mapTo(requestDto, Section.class)).thenReturn(section);
        when(sectionRepository.save(any(Section.class))).thenReturn(savedSection);
        when(mapper.mapTo(savedSection, SectionResponseDTO.class)).thenReturn(expectedDto);

        SectionResponseDTO result = sectionService.create(courseId, requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Section", result.getTitle());
        assertEquals("New Description", result.getDescription());

        verify(courseService).findById(courseId);
        verify(sectionRepository).save(any(Section.class));
    }

    @Test
    void update_ShouldUpdateExistingSection() {
        Long courseId = 1L;
        Long sectionId = 1L;

        Course course = new Course();
        course.setId(courseId);

        SectionRequestDTO requestDto = new SectionRequestDTO();
        requestDto.setTitle("Updated Section");
        requestDto.setDescription("Updated Description");

        Section existingSection = new Section();
        existingSection.setId(sectionId);
        existingSection.setTitle("Old Title");
        existingSection.setDescription("Old Description");
        existingSection.setCourse(course);
        existingSection.setContents(new ArrayList<>());

        Section updatedSection = new Section();
        updatedSection.setId(sectionId);
        updatedSection.setTitle("Updated Section");
        updatedSection.setDescription("Updated Description");
        updatedSection.setCourse(course);
        updatedSection.setContents(new ArrayList<>());

        SectionResponseDTO expectedDto = new SectionResponseDTO();
        expectedDto.setId(sectionId);
        expectedDto.setTitle("Updated Section");
        expectedDto.setDescription("Updated Description");

        when(inoveUtils.getNullPropertyNames(any(Section.class)))
                .thenReturn(new String[0]);
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId))
                .thenReturn(Optional.of(existingSection));
        when(mapper.mapTo(requestDto, Section.class)).thenReturn(updatedSection);
        when(sectionRepository.save(any(Section.class))).thenReturn(updatedSection);
        when(mapper.mapTo(updatedSection, SectionResponseDTO.class)).thenReturn(expectedDto);

        // Act
        SectionResponseDTO result = sectionService.update(courseId, sectionId, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Section", result.getTitle());
        assertEquals("Updated Description", result.getDescription());

        verify(sectionRepository).findByIdAndCourseId(sectionId, courseId);
        verify(sectionRepository).save(any(Section.class));
    }

    @Test
    void update_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        Long courseId = 1L;
        Long sectionId = 1L;

        Course course = new Course();
        course.setId(courseId);

        SectionRequestDTO requestDto = new SectionRequestDTO();
        requestDto.setTitle("Updated Section");
        // Description == null - Não deve ser atualizada

        Section existingSection = new Section();
        existingSection.setId(sectionId);
        existingSection.setTitle("Old Title");
        existingSection.setDescription("Existing Description");
        existingSection.setCourse(course);
        existingSection.setContents(new ArrayList<>());

        Section updatedSection = new Section();
        updatedSection.setId(sectionId);
        updatedSection.setTitle("Updated Section");
        updatedSection.setDescription("Existing Description");
        updatedSection.setCourse(course);
        updatedSection.setContents(new ArrayList<>());

        SectionResponseDTO expectedDto = new SectionResponseDTO();
        expectedDto.setId(sectionId);
        expectedDto.setTitle("Updated Section");
        expectedDto.setDescription("Existing Description");

        when(inoveUtils.getNullPropertyNames(any(Section.class)))
                .thenReturn(new String[]{"description"});
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId))
                .thenReturn(Optional.of(existingSection));
        when(mapper.mapTo(requestDto, Section.class)).thenReturn(updatedSection);
        when(sectionRepository.save(any(Section.class))).thenReturn(updatedSection);
        when(mapper.mapTo(updatedSection, SectionResponseDTO.class)).thenReturn(expectedDto);

        SectionResponseDTO result = sectionService.update(courseId, sectionId, requestDto);

        assertNotNull(result);
        assertEquals("Updated Section", result.getTitle());
        assertEquals("Existing Description", result.getDescription());

        verify(sectionRepository).findByIdAndCourseId(sectionId, courseId);
        verify(inoveUtils).getNullPropertyNames(any(Section.class));
        verify(sectionRepository).save(any(Section.class));
    }

    @Test
    void deleteById_ShouldDeleteSection_WhenExists() {
        Long courseId = 1L;
        Long sectionId = 1L;

        Course course = new Course();
        course.setId(courseId);

        Section section = new Section();
        section.setId(sectionId);
        section.setCourse(course);
        section.setContents(new ArrayList<>());

        when(sectionRepository.findByIdAndCourseId(sectionId, courseId))
                .thenReturn(Optional.of(section));

        sectionService.deleteById(courseId, sectionId);

        verify(sectionRepository).findByIdAndCourseId(sectionId, courseId);
        verify(sectionRepository).delete(section);
    }

    @Test
    void deleteById_ShouldThrowException_WhenSectionNotFound() {
        Long courseId = 1L;
        Long sectionId = 1L;

        when(sectionRepository.findByIdAndCourseId(sectionId, courseId))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
                sectionService.deleteById(courseId, sectionId));

        verify(sectionRepository).findByIdAndCourseId(sectionId, courseId);
        verify(sectionRepository, never()).delete(any());
    }
}