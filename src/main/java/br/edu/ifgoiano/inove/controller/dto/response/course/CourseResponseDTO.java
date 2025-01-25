package br.edu.ifgoiano.inove.controller.dto.response.course;

import br.edu.ifgoiano.inove.controller.dto.request.course.FeedBackOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.StudentSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
    private List<StudentSimpleResponseDTO> student;
    private List<UserSimpleResponseDTO> admins;
    private List<UserSimpleResponseDTO> instructors;
    private List<FeedBackOutputDTO> feedBacks;
    private List<SectionSimpleResponseDTO> sections;
}
