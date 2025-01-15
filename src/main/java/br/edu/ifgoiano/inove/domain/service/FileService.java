package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;

import java.io.IOException;
import java.io.InputStream;

public interface FileService {
    String upload(Long courseId,
                  Long sectionId,
                  ContentSimpleRequestDTO contentDTO) throws IOException;

    InputStream stream(String fileName);

    void delete(Long courseId, Long sectionId, Long contentId);
}
