package br.edu.ifgoiano.inove.controller.dto.response.user;

import br.edu.ifgoiano.inove.controller.dto.response.school.SchoolResponseDTO;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private Long id;

    private String name;

    private String cpf;

    private String email;

    private UserRole role;

    private Date birthDate;

    private SchoolResponseDTO school;
}
