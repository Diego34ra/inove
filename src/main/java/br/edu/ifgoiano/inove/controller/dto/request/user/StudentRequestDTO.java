package br.edu.ifgoiano.inove.controller.dto.request.user;

import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.model.School;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    private String name;
    @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos")
    private String cpf;
    @Email(message = "O email é inválido")
    private String email;
    private String password;
    private Date birthDate;
    private School school;
    private List<Course> student_courses;
    private UserRole role = UserRole.STUDENT;
}
