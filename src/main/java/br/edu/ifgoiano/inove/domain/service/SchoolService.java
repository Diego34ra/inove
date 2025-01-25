package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.request.school.SchoolRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.school.SchoolResponseDTO;
import br.edu.ifgoiano.inove.domain.model.School;

import java.util.List;

public interface SchoolService {
    List<SchoolResponseDTO> list();

    SchoolResponseDTO findOneById(Long id);

    School findById(Long id);

    SchoolResponseDTO create (SchoolRequestDTO newSchoolDTO);

    SchoolResponseDTO update (Long id, SchoolRequestDTO schoolDTO);

    void deleteById(Long Id);
}
