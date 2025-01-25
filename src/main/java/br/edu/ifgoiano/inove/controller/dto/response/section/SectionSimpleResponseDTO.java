package br.edu.ifgoiano.inove.controller.dto.response.section;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class SectionSimpleResponseDTO {
    private Long id;

    private String title;

    private String description;
}
