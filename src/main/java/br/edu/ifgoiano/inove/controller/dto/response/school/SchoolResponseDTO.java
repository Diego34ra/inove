package br.edu.ifgoiano.inove.controller.dto.response.school;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String city;
    private String federativeUnit;
}
