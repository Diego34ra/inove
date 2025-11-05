package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.user.InstructorRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.request.user.StudentRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.request.user.UserRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.school.SchoolResponseDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${frontend.url}")
    private String frontendUrl;

    private final Map<String, InstructorRequestDTO> pendingInstructors = new HashMap<>();

    @Override
    public List<UserSimpleResponseDTO> list() {
        return mapper.toList(userRepository.findAll(), UserSimpleResponseDTO.class);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi possível encontrar nenhum usuario com esse id."));
    }


    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi possível encontrar nenhum usuario com esse id."));
    }

    @Override
    public UserResponseDTO findOneById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não foi possível encontrar nenhum usuario com esse id."));
        return mapper.mapTo(user, UserResponseDTO.class);
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO newUserDTO) {
        User newUser = mapper.mapTo(newUserDTO, User.class);

        if (emailExists(newUser.getEmail()))
            throw new ResourceBadRequestException("Esse email já está cadastrado!");

        if (cpfExists(newUser.getCpf()))
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

        if (cpfExists(userCreate.getCpf()))
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
        try {
            User user = findById(id);
            userRepository.delete(user);
        } catch (DataIntegrityViolationException ex) {
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

        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceBadRequestException("CPF já cadastrado.");
        }

        if (requestRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceBadRequestException("Já existe uma solicitação de cadastro com este e-mail.");
        }

        if (requestRepository.existsByCpf(dto.getCpf())) {
            throw new ResourceBadRequestException("Já existe uma solicitação de cadastro com este CPF.");
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

        String approveLink = frontendUrl + "/confirmar-instrutor?token=" + token;
        String emailBody = String.format(
                "<!DOCTYPE html>" +
                "<html lang='pt-BR'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <meta http-equiv='X-UA-Compatible' content='IE=edge'>" +
                "    <meta name='format-detection' content='telephone=no'>" +
                "    <meta name='format-detection' content='date=no'>" +
                "    <meta name='format-detection' content='address=no'>" +
                "    <meta name='format-detection' content='email=no'>" +
                "    <meta name='x-apple-disable-message-reformatting'>" +
                "    <title>Nova Solicitação de Instrutor - Inove</title>" +
                "    <link href='https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap' rel='stylesheet'>" +
                "    <style>" +
                "        body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #3261a8 0%%, #2a5190 100%%); padding: 40px 30px; text-align: center; color: white; border-radius: 10px 10px 0 0; }" +
                "        .logo { max-width: 180px; height: auto; margin-bottom: 20px; }" +
                "        .content { background: #ffffff; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                "        .info-box { background: #f9f9f9; padding: 20px; margin: 20px 0; border-radius: 8px; border: 1px solid #e0e0e0; }" +
                "        .info-item { margin: 15px 0; padding: 10px; border-left: 4px solid #3261a8; background: white; }" +
                "        .info-label { font-weight: bold; color: #3261a8; display: block; margin-bottom: 5px; font-size: 13px; }" +
                "        .info-value { color: #333; font-size: 15px; }" +
                "        .motivation { background: #fff3e6; padding: 20px; border-radius: 8px; border-left: 4px solid #f9a826; margin: 20px 0; }" +
                "        .button { display: inline-block; padding: 15px 35px; background: linear-gradient(135deg, #3261a8 0%%, #2a5190 100%%); color: white !important; text-decoration: none; border-radius: 8px; font-weight: bold; margin: 20px 0; text-align: center; box-shadow: 0 4px 6px rgba(50, 97, 168, 0.3); transition: all 0.3s ease; }" +
                "        .button:hover { opacity: 0.9; transform: translateY(-2px); }" +
                "        .footer { text-align: center; margin-top: 30px; padding: 20px; color: #999; font-size: 12px; background: #f9f9f9; border-radius: 8px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <img src='https://raw.githubusercontent.com/joaog0liveira/Inove-Painel/733a50f255b7a07bdb7e90881206d81794072538/src/assets/logowhite.png' alt='Inove Logo' class='logo'>" +
                "            <h1 style='margin: 0; font-size: 28px;'>Nova Solicitação de Instrutor</h1>" +
                "            <p style='margin: 10px 0 0 0; opacity: 0.9; font-size: 16px;'>Um novo candidato deseja se tornar instrutor na plataforma</p>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <div class='info-box'>" +
                "                <h2 style='color: #f9a826; margin-top: 0; font-size: 22px;'>Dados do Candidato</h2>" +
                "                <div class='info-item'>" +
                "                    <span class='info-label'>NOME</span>" +
                "                    <span class='info-value'>%s</span>" +
                "                </div>" +
                "                <div class='info-item'>" +
                "                    <span class='info-label'>E-MAIL</span>" +
                "                    <span class='info-value'>%s</span>" +
                "                </div>" +
                "                <div class='info-item'>" +
                "                    <span class='info-label'>CPF</span>" +
                "                    <span class='info-value'>%s</span>" +
                "                </div>" +
                "            </div>" +
                "            <div class='motivation'>" +
                "                <h3 style='margin-top: 0; color: #c57d0d; font-size: 18px;'>Motivação</h3>" +
                "                <p style='margin: 0; color: #8a5a08; font-size: 14px; line-height: 1.6;'>%s</p>" +
                "            </div>" +
                "            <div style='text-align: center; margin-top: 30px;'>" +
                "                <a href='%s' class='button'>APROVAR CADASTRO</a>" +
                "                <p style='margin-top: 15px; font-size: 13px; color: #999;'>⏰ Este link expira em 48 horas</p>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p style='margin: 5px 0;'>Este é um e-mail automático, por favor não responda.</p>" +
                "            <p style='margin: 5px 0;'>© 2025 Plataforma Inove - Todos os direitos reservados</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>",
                dto.getName(), dto.getEmail(), dto.getCpf(), dto.getMotivation(), approveLink
        );

        emailService.sendHtml(adminEmail, "🎓 Novo Cadastro de Instrutor", emailBody);
    }

    @Override
    @Transactional
    public void confirmInstructorRegistrationByToken(String token) {
        InstructorRequest req = requestRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de cadastro não encontrada."));

        if (req.getStatus() == RequestStatus.APPROVED) {
            throw new ResourceBadRequestException("Este instrutor já foi confirmado anteriormente.");
        }

        if (req.getExpiresAt().isBefore(Instant.now())) {
            req.setStatus(RequestStatus.EXPIRED);
            requestRepository.save(req);
            throw new ResourceBadRequestException("Token expirado.");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            req.setStatus(RequestStatus.APPROVED);
            req.setApprovedAt(Instant.now());
            requestRepository.save(req);
            throw new ResourceBadRequestException("E-mail já cadastrado.");
        }

        if (userRepository.existsByCpf(req.getCpf())) {
            throw new ResourceBadRequestException("CPF já cadastrado.");
        }

        String temporaryPassword = RandomStringUtils.randomAlphanumeric(8);
        String encryptedPassword = new BCryptPasswordEncoder().encode(temporaryPassword);

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
                "✅ Cadastro de Instrutor Confirmado",
                String.format(
                        "<!DOCTYPE html>" +
                        "<html lang='pt-BR'>" +
                        "<head>" +
                        "    <meta charset='UTF-8'>" +
                        "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "    <meta http-equiv='X-UA-Compatible' content='IE=edge'>" +
                        "    <meta name='format-detection' content='telephone=no'>" +
                        "    <meta name='format-detection' content='date=no'>" +
                        "    <meta name='format-detection' content='address=no'>" +
                        "    <meta name='format-detection' content='email=no'>" +
                        "    <meta name='x-apple-disable-message-reformatting'>" +
                        "    <title>Cadastro de Instrutor Confirmado - Inove</title>" +
                        "    <link href='https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap' rel='stylesheet'>" +
                        "    <style>" +
                        "        body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                        "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                        "        .header { background: linear-gradient(135deg, #3261a8 0%%, #2a5190 100%%); padding: 40px 30px; text-align: center; color: white; border-radius: 10px 10px 0 0; }" +
                        "        .logo { max-width: 180px; height: auto; margin-bottom: 20px; }" +
                        "        .content { background: #ffffff; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                        "        .success-box { background: #f9f9f9; padding: 30px; margin: 20px 0; border-radius: 10px; border-left: 5px solid #3261a8; text-align: center; border: 1px solid #e0e0e0; }" +
                        "        .icon { width: 64px; height: 64px; margin-bottom: 15px; }" +
                        "        .info-badge { display: inline-block; background: white; padding: 10px 20px; border-radius: 20px; margin: 10px 5px; border: 1px solid #e0e0e0; }" +
                        "        .footer { text-align: center; margin-top: 30px; padding: 20px; color: #999; font-size: 12px; background: #f9f9f9; border-radius: 8px; }" +
                        "    </style>" +
                        "</head>" +
                        "<body>" +
                        "    <div class='container'>" +
                        "        <div class='header'>" +
                        "            <img src='https://raw.githubusercontent.com/joaog0liveira/Inove-Painel/733a50f255b7a07bdb7e90881206d81794072538/src/assets/logowhite.png' alt='Inove Logo' class='logo'>" +
                        "            <h1 style='margin: 0; font-size: 28px;'>Confirmação Realizada</h1>" +
                        "            <p style='margin: 10px 0 0 0; opacity: 0.9; font-size: 16px;'>Novo instrutor aprovado com sucesso</p>" +
                        "        </div>" +
                        "        <div class='content'>" +
                        "            <div class='success-box'>" +
                        "                <img src='https://cdn-icons-png.flaticon.com/512/5610/5610944.png' alt='Success' class='icon'>" +
                        "                <h2 style='color: #3261a8; margin: 10px 0; font-size: 24px;'>Cadastro Aprovado!</h2>" +
                        "                <p style='color: #333; margin: 15px 0; font-size: 16px;'>O instrutor foi adicionado à plataforma com sucesso.</p>" +
                        "                <div style='margin-top: 20px;'>" +
                        "                    <div class='info-badge'>" +
                        "                        <strong style='color: #3261a8;'>Nome:</strong> <span style='color: #333;'>%s</span>" +
                        "                    </div>" +
                        "                    <div class='info-badge'>" +
                        "                        <strong style='color: #3261a8;'>E-mail:</strong> <span style='color: #333;'>%s</span>" +
                        "                    </div>" +
                        "                </div>" +
                        "            </div>" +
                        "            <div style='background: #f9f9f9; padding: 20px; border-radius: 8px; margin-top: 20px; border-left: 4px solid #3261a8; border: 1px solid #e0e0e0;'>" +
                        "                <h3 style='margin-top: 0; color: #3261a8; font-size: 18px;'>Notificação Enviada</h3>" +
                        "                <p style='margin: 0; color: #333; font-size: 14px;'>O instrutor recebeu um e-mail com as credenciais de acesso e instruções para começar.</p>" +
                        "            </div>" +
                        "        </div>" +
                        "        <div class='footer'>" +
                        "            <p style='margin: 5px 0;'>Este é um e-mail automático, por favor não responda.</p>" +
                        "            <p style='margin: 5px 0;'>© 2025 Plataforma Inove - Todos os direitos reservados</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>",
                        req.getName(), req.getEmail())
        );

        String instructorEmailBody = String.format(
                "<!DOCTYPE html>" +
                "<html lang='pt-BR'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <meta http-equiv='X-UA-Compatible' content='IE=edge'>" +
                "    <meta name='format-detection' content='telephone=no'>" +
                "    <meta name='format-detection' content='date=no'>" +
                "    <meta name='format-detection' content='address=no'>" +
                "    <meta name='format-detection' content='email=no'>" +
                "    <meta name='x-apple-disable-message-reformatting'>" +
                "    <title>Bem-vindo à Plataforma Inove</title>" +
                "    <link href='https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap' rel='stylesheet'>" +
                "    <style>" +
                "        body { font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #3261a8 0%%, #2a5190 100%%); padding: 40px 30px; text-align: center; color: white; border-radius: 10px 10px 0 0; }" +
                "        .logo { max-width: 180px; height: auto; margin-bottom: 20px; }" +
                "        .content { background: #ffffff; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                "        .welcome-box { background: #f9f9f9; padding: 30px; margin: 20px 0; border-radius: 10px; text-align: center; border: 1px solid #e0e0e0; }" +
                "        .credentials-box { background: #f9f9f9; padding: 25px; margin: 20px 0; border-radius: 10px; border-left: 5px solid #3261a8; border: 1px solid #e0e0e0; }" +
                "        .credential-item { margin: 15px 0; padding: 15px; background: white; border-radius: 8px; border: 1px solid #e0e0e0; }" +
                "        .label { font-weight: bold; color: #3261a8; display: block; margin-bottom: 8px; font-size: 12px; text-transform: uppercase; letter-spacing: 1px; }" +
                "        .value { color: #333; font-size: 18px; font-family: 'Courier New', monospace; font-weight: bold; }" +
                "        .alert-box { background: #fff3e6; padding: 20px; border-radius: 8px; border-left: 5px solid #f9a826; margin: 20px 0; }" +
                "        .button { display: inline-block; padding: 15px 40px; background: linear-gradient(135deg, #3261a8 0%%, #2a5190 100%%); color: white !important; text-decoration: none; border-radius: 8px; font-weight: bold; margin: 20px 0; box-shadow: 0 4px 6px rgba(50, 97, 168, 0.3); transition: all 0.3s ease; }" +
                "        .button:hover { opacity: 0.9; transform: translateY(-2px); }" +
                "        .steps-box { background: #f9f9f9; padding: 25px; margin: 20px 0; border-radius: 10px; border: 1px solid #e0e0e0; }" +
                "        .step-item { padding: 12px 0; border-bottom: 1px solid #e0e0e0; }" +
                "        .step-item:last-child { border-bottom: none; }" +
                "        .footer { text-align: center; margin-top: 30px; padding: 20px; color: #999; font-size: 12px; background: #f9f9f9; border-radius: 8px; }" +
                "        .icon { width: 64px; height: 64px; margin-bottom: 15px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <img src='https://raw.githubusercontent.com/joaog0liveira/Inove-Painel/733a50f255b7a07bdb7e90881206d81794072538/src/assets/logowhite.png' alt='Inove Logo' class='logo'>" +
                "            <h1 style='margin: 0; font-size: 32px;'>Bem-vindo à Plataforma Inove!</h1>" +
                "            <p style='margin: 10px 0 0 0; opacity: 0.95; font-size: 18px;'>Seu cadastro foi aprovado com sucesso</p>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <div class='welcome-box'>" +
                "                <h2 style='color: #3261a8; margin-top: 0; font-size: 26px;'>Olá, %s!</h2>" +
                "                <p style='font-size: 16px; color: #555; margin: 15px 0;'>Estamos muito felizes em tê-lo(a) como instrutor(a) na nossa plataforma!</p>" +
                "                <p style='font-size: 15px; color: #666;'>Agora você pode começar a criar cursos incríveis e compartilhar seu conhecimento com milhares de estudantes.</p>" +
                "            </div>" +
                "            <div class='credentials-box'>" +
                "                <h3 style='margin-top: 0; color: #3261a8; font-size: 20px;'>Seus Dados de Acesso</h3>" +
                "                <div class='credential-item'>" +
                "                    <span class='label'>E-mail de Login</span>" +
                "                    <span class='value'>%s</span>" +
                "                </div>" +
                "                <div class='credential-item'>" +
                "                    <span class='label'>Senha Temporária</span>" +
                "                    <span class='value'>%s</span>" +
                "                </div>" +
                "            </div>" +
                "            <div class='alert-box'>" +
                "                <h4 style='margin-top: 0; color: #c57d0d; font-size: 18px;'>Importante</h4>" +
                "                <p style='margin: 0; color: #8a5a08; font-size: 15px;'><strong>Por segurança, recomendamos que você altere sua senha após o primeiro login.</strong></p>" +
                "            </div>" +
                "            <div style='text-align: center; margin-top: 30px;'>" +
                "                <a href='%s/login' class='button'>ACESSAR PLATAFORMA</a>" +
                "            </div>" +
                "            <div class='steps-box'>" +
                "                <h3 style='color: #3261a8; margin-top: 0; font-size: 20px;'>Próximos Passos</h3>" +
                "                <div class='step-item'>" +
                "                    <strong style='color: #3261a8;'>1.</strong> <span style='color: #555;'>Faça login na plataforma com suas credenciais</span>" +
                "                </div>" +
                "                <div class='step-item'>" +
                "                    <strong style='color: #3261a8;'>2.</strong> <span style='color: #555;'>Explore o painel do instrutor</span>" +
                "                </div>" +
                "                <div class='step-item'>" +
                "                    <strong style='color: #3261a8;'>3.</strong> <span style='color: #555;'>Adicione seus conteúdos e inspire alunos!</span>" +
                "                </div>" +
                "            </div>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p style='margin: 5px 0;'>Precisa de ajuda? <a href='mailto:inoveprojetointegrador@gmail.com' style='color: #3261a8; text-decoration: none;'>Entre em contato conosco</a>.</p>" +
                "            <p style='margin: 5px 0;'>© 2025 Plataforma Inove - Todos os direitos reservados</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>",
                req.getName(), req.getEmail(), temporaryPassword, frontendUrl
        );

        emailService.sendHtml(
                req.getEmail(),
                "🎉 Cadastro Aprovado - Bem-vindo à Plataforma Inove!",
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

    @Override
    public SchoolResponseDTO findSchoolByUserId(Long userId) {
        User user = findById(userId);
        if (user.getSchool() == null) {
            throw new ResourceNotFoundException("Usuário não está associado a nenhuma escola.");
        }
        return mapper.mapTo(user.getSchool(), SchoolResponseDTO.class);
    }
}
