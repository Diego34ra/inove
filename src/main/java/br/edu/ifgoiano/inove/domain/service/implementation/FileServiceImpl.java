package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;
import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.service.ContentService;
import br.edu.ifgoiano.inove.domain.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

    @Override
    public String upload(MultipartFile file, Long courseId, Long sectionId, ContentSimpleRequestDTO contentDTO) throws IOException {
//        MultipartFile file = contentDTO.getFile();

        Path tempFile = Files.createTempFile("temp-", file.getOriginalFilename());
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        String keyName = file.getOriginalFilename();
        s3Service.uploadFile(bucketName, keyName, tempFile.toFile());

        Files.delete(tempFile);

        Content newContent = mapper.mapTo(contentDTO, Content.class);
        newContent.setFileUrl("https://" + bucketName + ".s3.amazonaws.com/" + keyName);
        newContent.setFileName(keyName);

        contentService.create(courseId, sectionId, newContent);

        return "Sucesso no upload do arquivo!";
    }

    @Override
    public InputStream stream(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Service.getFileStream(getObjectRequest);
    }

    @Override
    public void delete(Long courseId, Long sectionId, Long contentId) {
        Content content = contentService.findById(sectionId, contentId);

        s3Service.deleteFile(bucketName, content.getFileName());

        contentService.deleteById(courseId, sectionId);

        System.out.println("Todas as referências do arquivo foram deletadas!");
    }

}
