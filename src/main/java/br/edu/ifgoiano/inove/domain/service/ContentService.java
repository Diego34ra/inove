package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.request.content.ContentRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentSimpleOutputDTO;
import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.model.Section;

import java.util.List;

public interface ContentService {
    List<ContentSimpleOutputDTO> list(Long contentId);

    Content findById(Long sectionId, Long contentId);

    ContentOutputDTO findOneById(Long sectionId, Long contentId);

    ContentOutputDTO create (Long courseId, Long sectionId, Content newContent);

    ContentOutputDTO update (Long courseId, Long sectionId, Long contentId, ContentRequestDTO newContent);

    void deleteById(Long sectionId, Long contentId);
}
