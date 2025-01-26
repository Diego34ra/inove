package br.edu.ifgoiano.inove.controller.dto.request.user;

import br.edu.ifgoiano.inove.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleRequestDTO {

    private Long id;

    private UserRole role;
}
