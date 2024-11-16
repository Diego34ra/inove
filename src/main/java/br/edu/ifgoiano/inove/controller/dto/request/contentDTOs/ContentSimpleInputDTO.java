package br.edu.ifgoiano.inove.controller.dto.request.contentDTOs;

import br.edu.ifgoiano.inove.domain.model.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentSimpleInputDTO {

    private String description;
    private String title;
    private ContentType contentType;
}