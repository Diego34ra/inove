package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.request.section.SectionRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionSimpleResponseDTO;
import br.edu.ifgoiano.inove.domain.model.Section;

import java.util.List;

public interface SectionService {
    List<SectionSimpleResponseDTO> list(Long courserId);

    Section findById(Long sectionId);

    SectionResponseDTO findOne(Long courseId, Long sectionId);

    SectionResponseDTO create (Long courseId, SectionRequestDTO newSectionDTO);

    SectionResponseDTO update (Long courseId, Long sectionId, SectionRequestDTO newSectionDTO);

    Section findByIdAndCursoId(Long courseId, Long sectionId);

    void deleteById(Long courseId, Long sectionId);
}
