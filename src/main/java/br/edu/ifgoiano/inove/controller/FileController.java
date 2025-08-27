package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.domain.service.FileService;
import br.edu.ifgoiano.inove.domain.service.implementation.S3ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/inove/cursos/secoes/conteudos")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private S3ServiceImpl s3Service;

    @Autowired
    private S3Client s3Client;

    private final String BUCKET_NAME = "inove-bucket-streaming";

    private static final Map<String, String> MIME_TYPES = new HashMap<>() {{
        put("mp4", "video/mp4");
        put("pdf", "application/pdf");
    }};

    @GetMapping("/stream/{fileName}")
    public ResponseEntity<Resource> streamFile(
            @PathVariable String fileName,
            HttpServletRequest request) {

        try {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String mimeType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

            long fileSize = s3Service.getObjectSize(BUCKET_NAME, fileName);

            String rangeHeader = request.getHeader("Range");
            long start = 0, end = fileSize - 1;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            }

            end = Math.min(end, fileSize - 1);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(fileName)
                    .range("bytes=" + start + "-" + end)
                    .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);

            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, mimeType);
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(end - start + 1));
            headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);

            return new ResponseEntity<>(resource, headers, (rangeHeader == null) ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
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


