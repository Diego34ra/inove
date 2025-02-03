package br.edu.ifgoiano.inove.controller.dto.response.course;

import br.edu.ifgoiano.inove.controller.dto.response.user.UserSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseSimpleResponseDTO {
    private Long id;
    private String description;
    private String name;
    private List<UserSimpleResponseDTO> instructors;
}
