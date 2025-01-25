package br.edu.ifgoiano.inove.controller.dto.request.course;

import br.edu.ifgoiano.inove.domain.model.FeedBack;
import br.edu.ifgoiano.inove.domain.model.Section;
import br.edu.ifgoiano.inove.domain.model.User;
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
public class CourseRequestDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
    private List<User> admins;
    private List<User> instructors;
    private List<FeedBack> feedBacks;
    private List<Section> sections;
}
