package br.edu.ifgoiano.inove.controller.dto.response.user;

import br.edu.ifgoiano.inove.controller.dto.response.school.SchoolResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSimpleResponseDTO {

    private Long id;
    private String name;
    private String email;
    private SchoolResponseDTO school;
}
