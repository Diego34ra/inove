package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.user.StudentRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.request.user.UserRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.school.SchoolResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.StudentResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.School;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import br.edu.ifgoiano.inove.domain.repository.UserRepository;
import br.edu.ifgoiano.inove.domain.service.SchoolService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SchoolService schoolService;

    @Mock
    private InoveUtils inoveUtils;

    @Mock
    private MyModelMapper mapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void list_ShouldReturnAllUsers() {
        User user1 = createUser(1L, "User 1", "123.456.789-01", "user1@email.com", UserRole.ADMINISTRATOR);
        User user2 = createUser(2L, "User 2", "987.654.321-01", "user2@email.com", UserRole.STUDENT);
        List<User> users = List.of(user1, user2);

        UserSimpleResponseDTO dto1 = new UserSimpleResponseDTO();
        dto1.setId(1L);
        dto1.setName("User 1");
        dto1.setEmail("user1@email.com");

        UserSimpleResponseDTO dto2 = new UserSimpleResponseDTO();
        dto2.setId(2L);
        dto2.setName("User 2");
        dto2.setEmail("user2@email.com");

        List<UserSimpleResponseDTO> expectedDtos = List.of(dto1, dto2);

        when(userRepository.findAll()).thenReturn(users);
        when(mapper.toList(users, UserSimpleResponseDTO.class)).thenReturn(expectedDtos);

        List<UserSimpleResponseDTO> result = userService.list();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());

        verify(userRepository).findAll();
        verify(mapper).toList(users, UserSimpleResponseDTO.class);
    }

    @Test
    void findOneById_ShouldReturnUser_WhenExists() {
        Long userId = 1L;
        User user = createUser(userId, "Test User", "123.456.789-01", "test@email.com", UserRole.ADMINISTRATOR);

        UserResponseDTO expectedDto = new UserResponseDTO();
        expectedDto.setId(userId);
        expectedDto.setName("Test User");
        expectedDto.setEmail("test@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.mapTo(user, UserResponseDTO.class)).thenReturn(expectedDto);

        UserResponseDTO result = userService.findOneById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(mapper).mapTo(user, UserResponseDTO.class);
    }

    @Test
    void create_ShouldCreateNewUser() {
        UserRequestDTO requestDto = new UserRequestDTO();
        requestDto.setName("New User");
        requestDto.setEmail("newuser@email.com");
        requestDto.setCpf("123.456.789-01");
        requestDto.setPassword("password123");

        User user = new User();
        user.setName("New User");
        user.setEmail("newuser@email.com");
        user.setCpf("123.456.789-01");
        user.setPassword("password123");
        user.setRole(UserRole.ADMINISTRATOR);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("New User");
        savedUser.setEmail("newuser@email.com");
        savedUser.setCpf("123.456.789-01");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(UserRole.ADMINISTRATOR);

        UserResponseDTO expectedDto = new UserResponseDTO();
        expectedDto.setId(1L);
        expectedDto.setName("New User");
        expectedDto.setEmail("newuser@email.com");
        expectedDto.setRole(UserRole.ADMINISTRATOR);


        when(mapper.mapTo(requestDto, User.class)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(mapper.mapTo(savedUser, UserResponseDTO.class)).thenReturn(expectedDto);


        UserResponseDTO result = userService.create(requestDto);

        assertNotNull(result, "O resultado não deve ser nulo");
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getEmail(), result.getEmail());
        assertEquals(expectedDto.getRole(), result.getRole());



        verify(userRepository).save(any(User.class));
        verify(mapper).mapTo(requestDto, User.class);
        verify(mapper).mapTo(savedUser, UserResponseDTO.class);
    }

    @Test
    void createStudent_ShouldCreateNewStudent() {
        Long schoolId = 1L;
        StudentRequestDTO requestDto = new StudentRequestDTO();
        requestDto.setName("New Student");
        requestDto.setEmail("student@email.com");
        requestDto.setCpf("123.456.789-01");
        requestDto.setPassword("password123");

        School school = new School();
        school.setId(schoolId);
        school.setName("Test School");

        SchoolResponseDTO schoolResponseDTO = new SchoolResponseDTO();
        schoolResponseDTO.setId(schoolId);
        schoolResponseDTO.setName("Test School");

        User student = new User();
        student.setName("New Student");
        student.setEmail("student@email.com");
        student.setCpf("123.456.789-01");
        student.setPassword("password123");
        student.setRole(UserRole.STUDENT);
        student.setSchool(school);

        StudentResponseDTO expectedDto = new StudentResponseDTO();
        expectedDto.setId(1L);
        expectedDto.setName("New Student");
        expectedDto.setEmail("student@email.com");
        expectedDto.setCpf("123.456.789-01");
        expectedDto.setRole(UserRole.STUDENT);
        expectedDto.setSchool(schoolResponseDTO);

        when(schoolService.findById(schoolId)).thenReturn(school);
        when(mapper.mapTo(requestDto, User.class)).thenReturn(student);
        when(userRepository.save(any(User.class))).thenReturn(student);
        when(mapper.mapTo(student, StudentResponseDTO.class)).thenReturn(expectedDto);

        StudentResponseDTO result = userService.create(schoolId, requestDto);

        assertNotNull(result);
        assertEquals("New Student", result.getName());
        assertEquals("student@email.com", result.getEmail());

        verify(schoolService).findById(schoolId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenExists() {
        Long userId = 1L;
        User user = createUser(userId, "Test User", "123.456.789-01", "test@email.com", UserRole.ADMINISTRATOR);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteById_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteById(userId));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }

    private User createUser(Long id, String name, String cpf, String email, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setCpf(cpf);
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setRole(role);
        user.setBirthDate(new Date());
        user.setStudent_courses(new ArrayList<>());
        user.setAdmin_courses(new ArrayList<>());
        user.setInstructor_courses(new ArrayList<>());
        return user;
    }
}
