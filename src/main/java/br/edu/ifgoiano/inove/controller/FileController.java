package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;
import br.edu.ifgoiano.inove.domain.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

@RestController
@RequestMapping("api/inove/cursos/secoes/conteudos")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload/{courseId}/{sectionId}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @PathVariable Long courseId,
                                             @PathVariable Long sectionId,
                                             ContentSimpleRequestDTO contentDTO) {
        try {
            return ResponseEntity.ok(fileService.upload(file, courseId, sectionId, contentDTO));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the file temporarily.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file to S3.");
        }
    }

    private static final Map<String, String> MIME_TYPES = new HashMap<>() {{
        put("mp4", "video/mp4");
        put("pdf", "application/pdf");
    }};

    @GetMapping("/stream/{fileName}")
    public ResponseEntity<?> streamFile(@PathVariable String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String mimeType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDispositionFormData("inline", fileName);

            InputStream fileStream = fileService.getFile(fileName);
            InputStreamResource inputStreamResource = new InputStreamResource(fileStream);

            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao tentar fazer streaming do arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/type/{fileName}")
    public ResponseEntity<?> getFileType(@PathVariable String fileName) {
        try {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String mimeType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

            return ResponseEntity.ok().body(Map.of("contentType", mimeType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter o tipo do arquivo: " + e.getMessage());
        }
    }

}


