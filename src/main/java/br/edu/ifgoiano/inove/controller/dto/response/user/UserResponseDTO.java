package br.edu.ifgoiano.inove.controller.dto.response.user;

import br.edu.ifgoiano.inove.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String name;

    private String cpf;

    private String email;

    private UserRole role;
}
