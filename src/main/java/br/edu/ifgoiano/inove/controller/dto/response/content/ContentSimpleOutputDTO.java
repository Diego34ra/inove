package br.edu.ifgoiano.inove.controller.dto.response.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentSimpleOutputDTO {

    private Long id;

    private String description;

    private String title;

    private String fileName;
}
