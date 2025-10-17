package br.edu.ifgoiano.inove.domain.service;

import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileService {
    String upload(MultipartFile file,
                  Long courseId,
                  Long sectionId,
                  ContentSimpleRequestDTO contentDTO) throws IOException;

    String updateContentFile(MultipartFile file,
                            Long courseId,
                            Long sectionId,
                            Long contentId,
                            ContentSimpleRequestDTO contentDTO) throws IOException;

    void delete(Long courseId, Long sectionId, Long contentId);

    String uploadCourseImage(MultipartFile file, Long courseId) throws IOException;

    String previewCourseImage(Long courseId) throws IOException;

    InputStream getFile(String fileName) throws IOException;
}
