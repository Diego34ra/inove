package br.edu.ifgoiano.inove.controller.dto.request.content;

import br.edu.ifgoiano.inove.domain.model.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContentSimpleRequestDTO {
    private String title;
    private String description;
    private ContentType contentType;
    private String fileUrl;
    private String fileName;

    public ContentSimpleRequestDTO(String title, String description, ContentType contentType) {
        this.title = title;
        this.description = description;
        this.contentType = contentType;
    }
}
