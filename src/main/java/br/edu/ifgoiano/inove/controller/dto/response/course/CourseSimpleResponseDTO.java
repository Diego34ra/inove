package br.edu.ifgoiano.inove.controller.dto.response.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseSimpleResponseDTO {
    private Long id;

    private String name;
}
