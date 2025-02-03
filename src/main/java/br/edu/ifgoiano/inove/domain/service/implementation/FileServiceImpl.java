package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.content.ContentRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.model.Course;
import br.edu.ifgoiano.inove.domain.service.ContentService;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

@Service
public class FileServiceImpl implements FileService{
    private final S3ServiceImpl s3Service;

    public FileServiceImpl(S3ServiceImpl s3Service) {
        this.s3Service = s3Service;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private MyModelMapper mapper;

    @Autowired
    private ContentService contentService;

    @Autowired
    private CourseService courseService;

    private final String BUCKET_NAME = "inove-bucket-streaming";

    @Override
    public String upload(MultipartFile file, Long courseId, Long sectionId, ContentSimpleRequestDTO contentDTO) throws IOException {
        String keyName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        String fileUrl = s3Service.uploadFile(bucketName, keyName, file.getInputStream());
        ContentRequestDTO newContentDTO = new ContentRequestDTO(
                contentDTO.getDescription(),
                contentDTO.getTitle(),
                contentDTO.getContentType(),
                fileUrl,
                keyName
        );
        contentService.create(courseId, sectionId, newContentDTO);
        return "Upload realizado com sucesso!";
    }


    @Override
    public void delete(Long courseId, Long sectionId, Long contentId) {
        Content content = contentService.findById(sectionId, contentId);

        s3Service.deleteFile(bucketName, content.getFileName());

        contentService.deleteById(courseId, sectionId);

        System.out.println("Todas as referências do arquivo foram deletadas!");
    }

    @Override
    public String uploadCourseImage(MultipartFile file, Long courseId) throws IOException {
        String fileName = "cursos/" + courseId + "/imagem-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();

        String imageUrl = s3Service.uploadFile(BUCKET_NAME, fileName, file.getInputStream());

        courseService.updateCourseImage(courseId, imageUrl);

        return imageUrl;
    }

    @Override
    public String previewCourseImage(Long courseId) {
        Course course = courseService.findById(courseId);

        if (course.getImageUrl() == null || course.getImageUrl().isEmpty()) {
            throw new RuntimeException("Imagem não encontrada para o curso.");
        }

        return course.getImageUrl();
    }

    @Override
    public InputStream getFile(String fileName) throws IOException {
        return s3Service.getFile(bucketName, fileName);
    }

}
