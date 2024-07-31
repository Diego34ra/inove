package br.edu.ifgoiano.inove.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class SecaoSimpleOutputDTO {
    private Long id;

    private String titulo;

    private String descricao;
}
