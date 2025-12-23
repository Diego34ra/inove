package br.edu.ifgoiano.inove.controller.dto.response.content.completedContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedContentResponseDTO {
    private BigDecimal completePercentage;
    private List<CompletedContentMinDTO> completedContents;
}
