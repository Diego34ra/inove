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
import br.edu.ifgoiano.inove.domain.model.*;
import br.edu.ifgoiano.inove.domain.repository.InstructorRequestRepository;
import br.edu.ifgoiano.inove.domain.repository.UserRepository;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.service.EmailService;
import br.edu.ifgoiano.inove.domain.service.SchoolService;
import br.edu.ifgoiano.inove.domain.service.UserService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final SchoolService schoolService;
    private final MyModelMapper mapper;
    private final InoveUtils inoveUtils;
    private final CourseService courseService;
    private final EmailService emailService;
    private final InstructorRequestRepository requestRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    private final String approveBaseUrl = "https://inove.blog.br/api/inove/usuarios/instrutor/confirmar?token=";

    private final Map<String, InstructorRequestDTO> pendingInstructors = new HashMap<>();

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
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
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
        User user = userRepository.findByIdWithStudentCourses(userId)
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
        boolean removed = user.getStudent_courses().removeIf(course -> course.getId().equals(courseId));

        if (!removed) {
            throw new ResourceNotFoundException("Curso não encontrado na lista do usuário.");
        }

        userRepository.save(user);
    }

    @Override
    public void processInstructorRequest(InstructorRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceBadRequestException("E-mail já cadastrado.");
        }

        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(2, ChronoUnit.DAYS);

        InstructorRequest req = new InstructorRequest();
        req.setName(dto.getName());
        req.setCpf(dto.getCpf());
        req.setEmail(dto.getEmail());
        req.setMotivation(dto.getMotivation());
        req.setToken(token);
        req.setExpiresAt(expiresAt);
        req.setStatus(RequestStatus.PENDING);
        requestRepository.save(req);

        String approveLink = approveBaseUrl + token;
        String emailBody = String.format(
                "Nome: %s%nCPF: %s%nE-mail: %s%nMotivação: %s%n%nClique para APROVAR: %s",
                dto.getName(), dto.getCpf(), dto.getEmail(), dto.getMotivation(), approveLink
        );

        emailService.send(adminEmail, "Novo Cadastro de Instrutor", emailBody);
    }

    @Override
    @Transactional
    public void confirmInstructorRegistrationByToken(String token) {
        InstructorRequest req = requestRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de cadastro não encontrada."));

        if (req.getStatus() == RequestStatus.APPROVED) return;

        if (req.getExpiresAt().isBefore(Instant.now())) {
            req.setStatus(RequestStatus.EXPIRED);
            requestRepository.save(req);
            throw new ResourceBadRequestException("Token expirado.");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            req.setStatus(RequestStatus.APPROVED);
            req.setApprovedAt(Instant.now());
            requestRepository.save(req);
            return;
        }

        String temporaryPassword = generateTempPassword();
        String encryptedPassword = passwordEncoder.encode(temporaryPassword);

        User newInstructor = new User();
        newInstructor.setName(req.getName());
        newInstructor.setCpf(req.getCpf());
        newInstructor.setEmail(req.getEmail());
        newInstructor.setPassword(encryptedPassword);
        newInstructor.setRole(UserRole.INSTRUCTOR);
        userRepository.save(newInstructor);

        req.setStatus(RequestStatus.APPROVED);
        req.setApprovedAt(Instant.now());
        requestRepository.save(req);

        emailService.sendHtml(
                adminEmail,
                "Cadastro de Instrutor Confirmado",
                String.format("O cadastro do instrutor %s (%s) foi confirmado com sucesso.",
                        req.getName(), req.getEmail())
        );

        String instructorEmailBody = String.format(
                "Olá, %s!%n%n" +
                        "Seu cadastro como instrutor na plataforma foi aprovado!%n%n" +
                        "Aqui estão seus dados de acesso:%n" +
                        "E-mail: %s%n" +
                        "Senha temporária: %s%n%n" +
                        "Recomendamos que você altere sua senha após o primeiro login.%n%n" +
                        "Bem-vindo à plataforma!",
                req.getName(), req.getEmail(), temporaryPassword
        );

        emailService.sendHtml(
                req.getEmail(),
                "Cadastro Aprovado - Bem-vindo à Plataforma!",
                instructorEmailBody
        );
    }

    @Override
    @Transactional
    public User updatePasswordByEmail(String email, String password) {
        User user = findUserByEmail(email);

        String encryptedPassword = new BCryptPasswordEncoder().encode(password);
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    private String generateTempPassword() {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom sr = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(alphabet.charAt(sr.nextInt(alphabet.length())));
        return sb.toString();
    }
}
