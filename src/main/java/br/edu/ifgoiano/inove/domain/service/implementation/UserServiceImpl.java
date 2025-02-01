package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.request.user.*;
import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.StudentResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceBadRequestException;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceInUseException;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import br.edu.ifgoiano.inove.domain.repository.UserRepository;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.service.SchoolService;
import br.edu.ifgoiano.inove.domain.service.UserService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private MyModelMapper mapper;

    @Autowired
    private InoveUtils inoveUtils;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public List<UserSimpleResponseDTO> list() {
        return mapper.toList(userRepository.findAll(), UserSimpleResponseDTO.class);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Não foi possível encontrar nenhum usuario com esse id."));
    }

    @Override
    public UserResponseDTO findOneById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Não foi possível encontrar nenhum usuario com esse id."));
        return mapper.mapTo(user, UserResponseDTO.class);
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO newUserDTO) {
        User newUser = mapper.mapTo(newUserDTO,User.class);

        if (emailExists(newUser.getEmail()))
            throw new ResourceBadRequestException("Esse email já está cadastrado!");

        if(cpfExists(newUser.getCpf()))
            throw new ResourceBadRequestException("Esse CPF á está cadastrado!");

        String encryptedPasswrod = new BCryptPasswordEncoder().encode(newUser.getPassword());
        newUser.setPassword(encryptedPasswrod);

        return mapper.mapTo(userRepository.save(newUser), UserResponseDTO.class);
    }

    @Override
    @Transactional
    public StudentResponseDTO create(Long schoolId, StudentRequestDTO newUserDTO) {

       var userCreate = mapper.mapTo(newUserDTO, User.class);
       userCreate.setSchool(schoolService.findById(schoolId));

       if (userCreate.getRole() == null)
           userCreate.setRole(UserRole.STUDENT);

       if (emailExists(userCreate.getEmail()))
           throw new ResourceBadRequestException("Esse email já está cadastrado!");

       if(cpfExists(userCreate.getCpf()))
           throw new ResourceBadRequestException("Esse CPF á está cadastrado!");

        String encryptedPasswrod = new BCryptPasswordEncoder().encode(userCreate.getPassword());
       userCreate.setPassword(encryptedPasswrod);

       userCreate.setSchool(userCreate.getSchool());

       return mapper.mapTo(userRepository.save(userCreate), StudentResponseDTO.class);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, User userUpdate) {
        User user = findById(id);
        BeanUtils.copyProperties(userUpdate, user, inoveUtils.getNullPropertyNames(userUpdate));
        return mapper.mapTo(userRepository.save(user), UserResponseDTO.class);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try{
            User user = findById(id);
            userRepository.delete(user);
        } catch (DataIntegrityViolationException ex){
            throw new ResourceInUseException("O usuário de ID %d esta em uso e não pode ser removido.");
        }
    }

    @Override
    public List<UserSimpleResponseDTO> listUserByRole(String role) {
        return mapper.toList(userRepository.findByRole(role), UserSimpleResponseDTO.class);
    }

    @Override
    public List<UserResponseDTO> listAdmins() {
        return mapper.toList(userRepository.findByRole(UserRole.ADMINISTRATOR.name())
                , UserResponseDTO.class);
    }

    @Override
    public List<StudentResponseDTO> listStudents() {
        return mapper.toList(userRepository.findByRole(UserRole.STUDENT.name())
                , StudentResponseDTO.class);
    }

    @Override
    public List<UserResponseDTO> listInstructors() {
        return mapper.toList(userRepository.findByRole(UserRole.INSTRUCTOR.name())
                , UserResponseDTO.class);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean cpfExists(String cpf) {
        return userRepository.existsByCpf(cpf);
    }

    @Override
    @Transactional
    public UserResponseDTO subscribeStudent(Long userId, Long courseId) {
        User user = findById(userId);
        Course course = courseService.findById(courseId);

        boolean alreadyEnrolled = user.getStudent_courses().stream()
                .anyMatch(c -> c.getId().equals(courseId));
        if (alreadyEnrolled) {
            throw new ResourceBadRequestException("Usuário já está inscrito neste curso.");
        }

        user.getStudent_courses().add(course);

        return mapper.mapTo(userRepository.save(user), UserResponseDTO.class);
    }


    @Override
    public List<CourseSimpleResponseDTO> getStudentCourses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para listar cursos."));

        return mapper.toList(user.getStudent_courses(), CourseSimpleResponseDTO.class);
    }


    @Override
    public UserDetails findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    @Override
    @Transactional
    public void removeCourseFromUser(Long userId, Long courseId) {
        User user = findById(userId);
        boolean removed = user.getStudent_courses().removeIf(course -> course.getId().equals(courseId)); // Remove o curso

        if (!removed) {
            throw new ResourceNotFoundException("Curso não encontrado na lista do usuário.");
        }

        userRepository.save(user);
    }

    @Override
    public void processInstructorRequest(InstructorRequestDTO instructorDTO) {
        if (userRepository.existsByEmail(instructorDTO.getEmail())) {
            throw new ResourceBadRequestException("E-mail já cadastrado.");
        }

        // Envia e-mail para o admin
        String emailBody = String.format(
                "Nome: %s\nCPF: %s\nE-mail: %s\nMotivação: %s\n\n" +
                        "Clique no link para confirmar o cadastro: http://localhost:8080/api/inove/usuarios/instructor/confirmar?email=%s",
                instructorDTO.getName(), instructorDTO.getCpf(), instructorDTO.getEmail(), instructorDTO.getMotivation(),
                instructorDTO.getEmail()
        );

        emailServiceImpl.sendConfirmationEmail(adminEmail, "Novo Cadastro de Instrutor", emailBody);
    }

    @Override
    @Transactional
    public void confirmInstructorRegistration(String email) {

        InstructorRequestDTO instructorDTO = new InstructorRequestDTO();

        User newInstructor = new User();
        newInstructor.setName(instructorDTO.getName());
        newInstructor.setCpf(instructorDTO.getCpf());
        newInstructor.setEmail(instructorDTO.getEmail());
        newInstructor.setPassword(new BCryptPasswordEncoder().encode("123456"));
        newInstructor.setRole(UserRole.INSTRUCTOR);

        userRepository.save(newInstructor);

        emailServiceImpl.sendConfirmationEmail(
                newInstructor.getEmail(),
                "Cadastro Confirmado",
                "Seu cadastro como instrutor foi confirmado com sucesso!"
        );
    }
}
