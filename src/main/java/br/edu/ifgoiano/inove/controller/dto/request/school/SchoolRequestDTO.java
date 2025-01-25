package br.edu.ifgoiano.inove.controller.dto.request.school;

import br.edu.ifgoiano.inove.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolRequestDTO {

    private Long id;
    private String name;
    private String city;
    private String email;
    private String password;
    private String federativeUnit;
    private List<User> student;
}
