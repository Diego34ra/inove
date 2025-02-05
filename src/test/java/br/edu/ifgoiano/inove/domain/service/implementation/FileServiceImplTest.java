package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;
import br.edu.ifgoiano.inove.domain.repository.ContentRepository;
import br.edu.ifgoiano.inove.domain.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private S3ServiceImpl s3Service;

    @Mock
    private MyModelMapper mapper;

    @Mock
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

    @TempDir
    Path tempDir;

    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "bucketName", BUCKET_NAME);
    }

//    @Test
//    void upload_ShouldSuccessfullyUploadFile() throws IOException {
//        Long courseId = 1L;
//        Long sectionId = 1L;
//        String fileName = "test-video.mp4";
//        byte[] content = "test video content".getBytes();
//
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                fileName,
//                "video/mp4",
//                content
//        );
//
//        ContentSimpleRequestDTO contentDTO = new ContentSimpleRequestDTO();
//        contentDTO.setFile(file);
//        contentDTO.setTitle("Test Video");
//
//        Content newContent = new Content();
//        newContent.setTitle("Test Video");
//
//        ContentOutputDTO contentOutputDTO = new ContentOutputDTO();
//        contentOutputDTO.setTitle("Test Video");
//
//        when(mapper.mapTo(contentDTO, Content.class)).thenReturn(newContent);
//        doNothing().when(s3Service).uploadFile(eq(BUCKET_NAME), eq(fileName), any(File.class));
//        when(contentService.create(eq(courseId), eq(sectionId), eq(newContent))).thenReturn(contentOutputDTO);
//
//        String result = fileService.upload(courseId, sectionId, contentDTO);
//
//        assertNotNull(result);
//        assertEquals("Sucesso no upload do arquivo!", result);
//        verify(s3Service).uploadFile(eq(BUCKET_NAME), eq(fileName), any(File.class));
//        verify(contentService).create(courseId, sectionId, newContent);
//        verify(mapper).mapTo(contentDTO, Content.class);
//    }
//
//    @Test
//    void stream_ShouldReturnInputStream() {
//        String fileName = "test-video.mp4";
//        InputStream expectedStream = new ByteArrayInputStream("test content".getBytes());
//
//        when(s3Service.getFile(any(GetObjectRequest.class))).thenReturn(expectedStream);
//
//        InputStream result = fileService.stream(fileName);
//
//        assertNotNull(result);
//        verify(s3Service).getFileStream(any(GetObjectRequest.class));
//    }

//    @Test
//    void delete_ShouldDeleteFileAndContent() {
//        Long courseId = 1L;
//        Long sectionId = 1L;
//        Long contentId = 1L;
//        String fileName = "test-video.mp4";
//
//        Content content = new Content();
//        content.setId(contentId);
//        content.setTitle("Content Title");
//        content.setFileName(fileName);
//
//        when(contentService.findById(eq(sectionId), eq(contentId))).thenReturn(content);
//        doNothing().when(s3Service).deleteFile(BUCKET_NAME, fileName);
//        doNothing().when(contentService).deleteById(courseId, sectionId);
//
//        fileService.delete(courseId, sectionId, contentId);
//
//        verify(contentService).findById(sectionId, contentId);
//        verify(s3Service).deleteFile(BUCKET_NAME, fileName);
//        verify(contentService).deleteById(courseId, sectionId);
//    }

//    @Test
//    void upload_ShouldHandleIOException() {
//        Long courseId = 1L;
//        Long sectionId = 1L;
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "test.mp4",
//                "video/mp4",
//                new byte[0]
//        );
//
//        ContentSimpleRequestDTO contentDTO = new ContentSimpleRequestDTO();
//        contentDTO.setFile(file);
//
//        doThrow(new RuntimeException("S3 Error")).when(s3Service)
//                .uploadFile(eq(BUCKET_NAME), any(), any(File.class));
//
//        assertThrows(RuntimeException.class, () ->
//                fileService.upload(courseId, sectionId, contentDTO));
//    }
//
//    @Test
//    void stream_ShouldHandleS3ServiceException() {
//        String fileName = "nonexistent.mp4";
//        when(s3Service.getFileStream(any(GetObjectRequest.class)))
//                .thenThrow(new RuntimeException("File not found"));
//
//        assertThrows(RuntimeException.class, () ->
//                fileService.stream(fileName));
//    }

    @Test
    void delete_ShouldHandleContentNotFound() {
        Long courseId = 1L;
        Long sectionId = 1L;
        Long contentId = 1L;

        assertThrows(RuntimeException.class, () ->
                fileService.delete(courseId, sectionId, contentId));

        verify(s3Service, never()).deleteFile(any(), any());
        verify(contentService, never()).deleteById(any(), any());
    }
}