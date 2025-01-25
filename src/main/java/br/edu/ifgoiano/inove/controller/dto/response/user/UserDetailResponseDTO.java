package br.edu.ifgoiano.inove.controller.dto.response.user;

import br.edu.ifgoiano.inove.domain.model.School;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserDetailResponseDTO {
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private Date birthDate;
    private School school;
    private UserRole role;
}
