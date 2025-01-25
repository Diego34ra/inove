package br.edu.ifgoiano.inove.controller.dto.response.section;

import br.edu.ifgoiano.inove.controller.dto.response.content.ContentSimpleOutputDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponseDTO {
    private Long id;
    private String title;
    private String description;
    private List<ContentSimpleOutputDTO> contents;
}
