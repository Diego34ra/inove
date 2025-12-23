package br.edu.ifgoiano.inove.controller.dto.response.content.completedContent;

import br.edu.ifgoiano.inove.domain.model.UserCompletedContent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedContentMinDTO {
    private Long courseId;
    private Long sectionId;
    private Long contentId;
    private Long userId;

    public CompletedContentMinDTO(UserCompletedContent completed) {
        this.courseId = completed.getCourse().getId();
        this.sectionId = completed.getSection().getId();
        this.contentId = completed.getContent().getId();
        this.userId = completed.getUser().getId();
    }
}
