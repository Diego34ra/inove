package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.school.SchoolRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.school.SchoolResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceInUseException;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.School;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.repository.SchoolRespository;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolServiceImplTest {

    @Mock
    private SchoolRespository schoolRepository;

    @Mock
    private MyModelMapper mapper;

    @Mock
    private InoveUtils inoveUtils;

    @InjectMocks
    private SchoolServiceImpl schoolService;

    private School school;
    private SchoolRequestDTO schoolRequestDTO;
    private SchoolResponseDTO schoolResponseDTO;

    @BeforeEach
    void setUp() {
        school = new School();
        school.setId(1L);
        school.setName("Test School");
        school.setCity("Test City");
        school.setEmail("test@school.com");
        school.setPassword("password123");
        school.setFederativeUnit("GO");
        school.setStudent(new ArrayList<>());

        schoolRequestDTO = new SchoolRequestDTO();

        schoolResponseDTO = new SchoolResponseDTO();
        schoolResponseDTO.setId(1L);
    }

    @Test
    void list_ShouldReturnAllSchools() {
        // Arrange
        List<School> schools = Arrays.asList(school);
        List<SchoolResponseDTO> expectedResponse = Arrays.asList(schoolResponseDTO);

        when(schoolRepository.findAll()).thenReturn(schools);
        when(mapper.toList(schools, SchoolResponseDTO.class)).thenReturn(expectedResponse);

        // Act
        List<SchoolResponseDTO> result = schoolService.list();

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.size(), result.size());
        verify(schoolRepository).findAll();
        verify(mapper).toList(schools, SchoolResponseDTO.class);
    }

    @Test
    void findOneById_WithValidId_ShouldReturnSchool() {
        // Arrange
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(mapper.mapTo(school, SchoolResponseDTO.class)).thenReturn(schoolResponseDTO);

        // Act
        SchoolResponseDTO result = schoolService.findOneById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(schoolResponseDTO.getId(), result.getId());
        verify(schoolRepository).findById(1L);
    }

    @Test
    void findOneById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> schoolService.findOneById(99L));
        verify(schoolRepository).findById(99L);
    }

    @Test
    void create_WithValidData_ShouldReturnCreatedSchool() {
        // Arrange
        School newSchool = new School();
        newSchool.setName("New School");
        newSchool.setCity("New City");
        newSchool.setEmail("new@school.com");
        newSchool.setPassword("newpass123");
        newSchool.setFederativeUnit("SP");

        when(mapper.mapTo(schoolRequestDTO, School.class)).thenReturn(newSchool);
        when(schoolRepository.save(any(School.class))).thenReturn(newSchool);
        when(mapper.mapTo(newSchool, SchoolResponseDTO.class)).thenReturn(schoolResponseDTO);

        // Act
        SchoolResponseDTO result = schoolService.create(schoolRequestDTO);

        // Assert
        assertNotNull(result);
        verify(schoolRepository).save(any(School.class));
        verify(mapper).mapTo(newSchool, SchoolResponseDTO.class);
    }

    @Test
    void update_WithValidData_ShouldReturnUpdatedSchool() {
        // Arrange
        School updatedSchool = new School();
        updatedSchool.setName("Updated School");
        updatedSchool.setCity("Updated City");
        updatedSchool.setEmail("updated@school.com");
        updatedSchool.setFederativeUnit("MG");

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(mapper.mapTo(schoolRequestDTO, School.class)).thenReturn(updatedSchool);
        when(schoolRepository.save(any(School.class))).thenReturn(updatedSchool);
        when(mapper.mapTo(updatedSchool, SchoolResponseDTO.class)).thenReturn(schoolResponseDTO);
        when(inoveUtils.getNullPropertyNames(any())).thenReturn(new String[]{});

        // Act
        SchoolResponseDTO result = schoolService.update(1L, schoolRequestDTO);

        // Assert
        assertNotNull(result);
        verify(schoolRepository).save(any(School.class));
        verify(mapper).mapTo(updatedSchool, SchoolResponseDTO.class);
    }

    @Test
    void update_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        School partialUpdate = new School();
        partialUpdate.setName("Updated Name");
        // Sómente quando o nome esta nulo ele retorna o nome

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(mapper.mapTo(schoolRequestDTO, School.class)).thenReturn(partialUpdate);
        when(schoolRepository.save(any(School.class))).thenReturn(school);
        when(mapper.mapTo(school, SchoolResponseDTO.class)).thenReturn(schoolResponseDTO);
        when(inoveUtils.getNullPropertyNames(any())).thenReturn(new String[]{"city", "email", "password", "federativeUnit"});

        // Act
        SchoolResponseDTO result = schoolService.update(1L, schoolRequestDTO);

        // Assert
        assertNotNull(result);
        verify(schoolRepository).save(any(School.class));
        verify(inoveUtils).getNullPropertyNames(partialUpdate);
    }

    @Test
    void update_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                schoolService.update(99L, schoolRequestDTO));
        verify(schoolRepository).findById(99L);
    }

    @Test
    void deleteById_WithValidId_ShouldDeleteSuccessfully() {
        // Arrange
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        doNothing().when(schoolRepository).delete(school);

        // Act
        schoolService.deleteById(1L);

        // Assert
        verify(schoolRepository).findById(1L);
        verify(schoolRepository).delete(school);
    }

    @Test
    void deleteById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                schoolService.deleteById(99L));
        verify(schoolRepository).findById(99L);
    }

    @Test
    void deleteById_WithSchoolHavingStudents_ShouldThrowResourceInUseException() {
        // Arrange
        School schoolWithStudents = new School();
        schoolWithStudents.setId(1L);
        schoolWithStudents.setStudent(Arrays.asList(new User())); 

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(schoolWithStudents));
        doThrow(DataIntegrityViolationException.class)
                .when(schoolRepository).delete(schoolWithStudents);

        // Act & Assert
        assertThrows(ResourceInUseException.class, () ->
                schoolService.deleteById(1L));
        verify(schoolRepository).findById(1L);
        verify(schoolRepository).delete(schoolWithStudents);
    }
}