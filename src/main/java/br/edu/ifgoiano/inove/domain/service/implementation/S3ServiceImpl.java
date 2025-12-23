package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;
import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.repository.ContentRepository;
import br.edu.ifgoiano.inove.domain.service.ContentService;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.service.FileService;
import br.edu.ifgoiano.inove.domain.service.SectionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Primary
public class S3ServiceImpl implements FileService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ContentService contentService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ContentRepository contentRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file, Long courseId, Long sectionId, ContentSimpleRequestDTO contentDTO) throws IOException {
        try {
            String fileName = generateFileName(file, courseId, sectionId);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);

            Content content = new Content();
            content.setTitle(contentDTO.getTitle());
            content.setDescription(contentDTO.getDescription());
            content.setContentType(contentDTO.getContentType());
            content.setFileName(fileName);
            content.setFileUrl(fileUrl);
            content.setSection(sectionService.findByIdAndCursoId(courseId, sectionId));

            contentRepository.save(content);

            courseService.saveUpdateDate(courseId);

            return "Conteúdo enviado com sucesso!";

        } catch (Exception e) {
            throw new IOException("Erro ao fazer upload do arquivo: " + e.getMessage(), e);
        }
    }

    @Override
    public String updateContentFile(MultipartFile file, Long courseId, Long sectionId, Long contentId, ContentSimpleRequestDTO contentDTO) throws IOException {
        Content existingContent = contentService.findById(courseId, sectionId, contentId);

        if (existingContent.getFileName() != null && !existingContent.getFileName().isEmpty()) {
            try {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(existingContent.getFileName())
                        .build();

                s3Client.deleteObject(deleteObjectRequest);
            } catch (Exception e) {
                throw new IOException("Erro ao deletar arquivo antigo: " + e.getMessage(), e);
            }
        }

        String newFileName = generateFileName(file, courseId, sectionId);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(newFileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String newFileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, newFileName);

        existingContent.setTitle(contentDTO.getTitle());
        existingContent.setDescription(contentDTO.getDescription());
        existingContent.setContentType(contentDTO.getContentType());
        existingContent.setFileName(newFileName);
        existingContent.setFileUrl(newFileUrl);

        contentRepository.save(existingContent);

        courseService.saveUpdateDate(courseId);

        return "Conteúdo e arquivo atualizados com sucesso!";
    }

    @Override
    @Transactional
    public void delete(Long courseId, Long sectionId, Long contentId) {
        Content content = contentService.findById(courseId, sectionId, contentId);

        if (content.getFileName() != null && !content.getFileName().isEmpty()) {
            try {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(content.getFileName())
                        .build();

                s3Client.deleteObject(deleteObjectRequest);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao deletar o arquivo do S3: " + e.getMessage(), e);
            }
        }

        contentService.deleteById(courseId, sectionId, contentId);
        courseService.saveUpdateDate(courseId);
    }

    @Override
    public String uploadCourseImage(MultipartFile file, Long courseId) throws IOException {
        Course course = courseService.findById(courseId);

        if (course.getImageUrl() != null && !course.getImageUrl().isEmpty()) {
            try {
                String oldImageKey = extractKeyFromUrl(course.getImageUrl());
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(oldImageKey)
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
            } catch (Exception e) {
                throw new IOException("Erro ao deletar imagem antiga: " + e.getMessage(), e);
            }
        }

        String imageFileName = generateCourseImageFileName(file, courseId);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(imageFileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String imageUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, imageFileName);

        courseService.updateCourseImage(courseId, imageUrl);

        return imageUrl;
    }

    @Override
    public String previewCourseImage(Long courseId) {
        return courseService.getCourseImageUrl(courseId);
    }

    @Override
    public InputStream getFile(String fileName) throws IOException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            return s3Client.getObject(getObjectRequest);

        } catch (Exception e) {
            throw new IOException("Erro ao buscar o arquivo: " + e.getMessage(), e);
        }
    }

    public String uploadFile(String bucketName, String keyName, InputStream inputStream) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build(), RequestBody.fromInputStream(inputStream, inputStream.available()));

        return "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
    }

    public InputStream getFile(String bucketName, String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    public void deleteFile(String bucketName, String keyName) {
        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(keyName).build());
    }

    public long getObjectSize(String bucketName, String keyName) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
        return headObjectResponse.contentLength();
    }

    private String generateFileName(MultipartFile file, Long courseId, Long sectionId) {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        return String.format("courses/%d/sections/%d/%s_%s%s",
                courseId, sectionId, timestamp, uniqueId, extension);
    }

    private String generateCourseImageFileName(MultipartFile file, Long courseId) {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        return String.format("courses/%d/images/%s_%s%s",
                courseId, timestamp, uniqueId, extension);
    }

    private String extractKeyFromUrl(String url) {
        if (url.contains(".com/")) {
            return url.substring(url.indexOf(".com/") + 5);
        }
        return url;
    }
}
