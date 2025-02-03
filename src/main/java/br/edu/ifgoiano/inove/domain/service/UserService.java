package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.request.user.*;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.StudentResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserSimpleResponseDTO;
import br.edu.ifgoiano.inove.domain.model.User;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    List<UserSimpleResponseDTO> list();

    User findById(Long id);

    UserResponseDTO findOneById(Long id);

    @Transactional
    UserResponseDTO create(UserRequestDTO newUser);

    @Transactional
    StudentResponseDTO create (Long schoolId, StudentRequestDTO newStudentDTO);

    @Transactional
    UserResponseDTO update (Long id, User user);

    void deleteById(Long Id);

    List<UserSimpleResponseDTO> listUserByRole(String role);

    List<UserResponseDTO> listAdmins();

    List<StudentResponseDTO> listStudents();

    List<UserResponseDTO> listInstructors();

    UserDetails findByEmail(String email);

    boolean emailExists(String email);

    boolean cpfExists(String cpf);

    UserResponseDTO subscribeStudent(Long userId, Long courseId);

    List<CourseSimpleResponseDTO> getStudentCourses(Long userId);

    void removeCourseFromUser(Long userId, Long courseId);

    void processInstructorRequest(InstructorRequestDTO instructorDTO);

    void confirmInstructorRegistration(String email);
}
