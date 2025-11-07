package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.response.content.completedContent.CompletedContentMinDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.completedContent.CompletedContentResponseDTO;

import java.util.List;

public interface UserCompletedContentService {
    CompletedContentMinDTO create (Long courseId, Long sectionId, Long contentId, Long userId);
    List<CompletedContentMinDTO> listContentCompletedDTO(Long courseId, Long userId);
    CompletedContentResponseDTO getUserProgress(Long courseId, Long userId);
    void deleteByContentId(Long contentId);
    void resetCourseProgress(Long courseId);
}
