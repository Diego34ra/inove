package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.domain.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/inove/cursos")
public class ImageCourseController {

    @Autowired
    private FileService fileService;

    @PostMapping("/{courseId}/upload-imagem-curso")
    public ResponseEntity<Map<String, String>> uploadCourseImage(@PathVariable Long courseId, @RequestParam("imagem") MultipartFile file) {
        try {
            String imageUrl = fileService.uploadCourseImage(file, courseId);

            // Criando um JSON válido para resposta
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Falha ao salvar a imagem."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Erro ao fazer upload para o S3."));
        }
    }


    @GetMapping("/{courseId}/preview-imagem")
    public ResponseEntity<Map<String, String>> previewImage(@PathVariable Long courseId) {
        try {
            String imagePreview = fileService.previewCourseImage(courseId);
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imagePreview);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Imagem não encontrada."));
        }
    }



}
