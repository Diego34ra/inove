package br.edu.ifgoiano.inove.controller.dto.request.content;

import br.edu.ifgoiano.inove.domain.model.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentSimpleRequestDTO {

    private String title;
    private String description;
    private ContentType contentType;
    private MultipartFile file;
}
