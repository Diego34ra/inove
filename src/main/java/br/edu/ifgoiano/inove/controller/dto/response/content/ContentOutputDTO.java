package br.edu.ifgoiano.inove.controller.dto.response.content;

import br.edu.ifgoiano.inove.domain.model.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentOutputDTO {
    private Long id;
    private String description;
    private String title;
    private ContentType contentType;
    private String fileUrl;
    private String fileName;
}
