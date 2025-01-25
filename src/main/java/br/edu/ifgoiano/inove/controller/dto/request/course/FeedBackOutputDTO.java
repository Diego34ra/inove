package br.edu.ifgoiano.inove.controller.dto.request.course;

import br.edu.ifgoiano.inove.controller.dto.response.user.StudentSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackOutputDTO {
    private Long id;
    private StudentSimpleResponseDTO student;
    private String comment;
}
