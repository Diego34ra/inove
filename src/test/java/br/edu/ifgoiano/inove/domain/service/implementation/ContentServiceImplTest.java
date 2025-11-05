package br.edu.ifgoiano.inove.domain.service.implementation;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.content.ContentRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentSimpleOutputDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.model.Section;
import br.edu.ifgoiano.inove.domain.repository.ContentRepository;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.service.SectionService;
import br.edu.ifgoiano.inove.domain.utils.InoveUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceImplTest {

    @InjectMocks
    private ContentServiceImpl contentService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private SectionService sectionService;

    @Mock
    private CourseService courseService;

    @Mock
    private MyModelMapper mapper;

    @Mock
    private InoveUtils utils;

    @Test
    void list() {
        Content content = new Content();
        content.setId(1L);
        content.setTitle("Conteudo Video");

        Content content2 = new Content();
        content2.setId(2L);
        content2.setTitle("Conteudo Texto");

        List<Content> categoriasMock = List.of(content, content2);

        ContentSimpleOutputDTO contentSimpleOutputDTO = new ContentSimpleOutputDTO();
        contentSimpleOutputDTO.setId(1L);
        contentSimpleOutputDTO.setTitle("Conteudo Video");

        ContentSimpleOutputDTO contentSimpleOutputDTO2 = new ContentSimpleOutputDTO();
        contentSimpleOutputDTO2.setId(2L);
        contentSimpleOutputDTO2.setTitle("Conteudo Texto");

        List<ContentSimpleOutputDTO> categoriasMockSimpleOutputDTOS = List.of(contentSimpleOutputDTO, contentSimpleOutputDTO2);

        when(contentRepository.findBySectionId(1L)).thenReturn(categoriasMock);
        when(mapper.toList(categoriasMock, ContentSimpleOutputDTO.class)).thenReturn(categoriasMockSimpleOutputDTOS);

        List<ContentSimpleOutputDTO> resultado = contentService.list(1L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Conteudo Video", resultado.get(0).getTitle());
        assertEquals("Conteudo Texto", resultado.get(1).getTitle());

        verify(contentRepository).findBySectionId(1L);
    }

    @Test
    void findOneById() {
        Long courseId = 1L;
        Long sectionId = 1L;
        Long contentId = 1L;

        Content existingContent = new Content();
        existingContent.setId(contentId);
        existingContent.setTitle("Test Content");

        ContentOutputDTO expectedOutput = new ContentOutputDTO();
        expectedOutput.setId(contentId);
        expectedOutput.setTitle("Test Content");

        when(contentRepository.findByIdAndSectionIdAndSection_Course_Id(contentId, sectionId, courseId))
                .thenReturn(Optional.of(existingContent));
        when(mapper.mapTo(existingContent, ContentOutputDTO.class))
                .thenReturn(expectedOutput);

        ContentOutputDTO result = contentService.findOneById(contentId, sectionId, courseId);

        assertNotNull(result, "The result should not be null");
        assertEquals(contentId, result.getId(), "Content ID should match");
        assertEquals("Test Content", result.getTitle(), "Content title should match");

        verify(contentRepository).findByIdAndSectionIdAndSection_Course_Id(contentId, sectionId, courseId);
        verify(mapper).mapTo(existingContent, ContentOutputDTO.class);
    }

    @Test
    void findOneById_WhenContentNotFound_ShouldThrowException() {
        Long courseId = 1L;
        Long sectionId = 1L;
        Long contentId = 1L;

        when(contentRepository.findByIdAndSectionIdAndSection_Course_Id(contentId, sectionId, courseId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            contentService.findOneById(courseId, sectionId, contentId);
        }, "Should throw exception when content not found");

        verify(contentRepository).findByIdAndSectionIdAndSection_Course_Id(contentId, sectionId, courseId);
    }

    @Test
    void findById() {
        Long courseId = 1L;
        Long sectionId = 1L;
        Long contentId = 1L;

        ContentRequestDTO updateContentDTO = new ContentRequestDTO();
        updateContentDTO.setTitle("Conteúdo Atualizado");

        Content existingContent = new Content();
        existingContent.setId(contentId);
        existingContent.setTitle("Conteúdo Original");
        when(contentRepository.findByIdAndSectionIdAndSection_Course_Id(contentId, sectionId, courseId)).thenReturn(Optional.of(existingContent));

        Content resultado = contentService.findById(courseId, sectionId, contentId);

        assertNotNull(resultado);

        verify(contentRepository).findByIdAndSectionIdAndSection_Course_Id(1L, 1L,1L);
    }

    @Test
    void create() {
        Content content = new Content();
        content.setTitle("Conteudo teste");

        ContentRequestDTO contentRequestDTO = new ContentRequestDTO();
        content.setTitle("Conteudo teste");

        Section section = new Section();
        section.setId(1L);
        section.setTitle("Seção teste");

        Content contentCreate = new Content();
        contentCreate.setId(1L);
        contentCreate.setTitle("Conteudo teste");

        ContentOutputDTO contentRetorno = new ContentOutputDTO();
        contentRetorno.setId(1L);
        contentRetorno.setTitle("Conteudo teste");

        when(sectionService.findByIdAndCursoId(1L, 1L)).thenReturn(section);
        when(contentRepository.save(content)).thenReturn(contentCreate);
        when(mapper.mapTo(contentRequestDTO, Content.class)).thenReturn(content);
        when(mapper.mapTo(contentCreate, ContentOutputDTO.class)).thenReturn(contentRetorno);

        ContentOutputDTO resultado = contentService.create(1L,1L,contentRequestDTO);

        assertNotNull(resultado);
        assertEquals(resultado.getId(),contentRetorno.getId(),"O ID do conteúdo retornado está incorreto");

        verify(sectionService).findByIdAndCursoId(1L, 1L);
        verify(mapper).mapTo(contentCreate, ContentOutputDTO.class);
        verify(contentRepository).save(content);
    }

    @Test
    void update() {
        Long courseId = 1L;
        Long sectionId = 1L;
        Long contentId = 1L;

        ContentRequestDTO updateContentDTO = new ContentRequestDTO();
        updateContentDTO.setTitle("Conteúdo Atualizado");

        Content existingContent = new Content();
        existingContent.setId(contentId);
        existingContent.setTitle("Conteúdo Original");

        Content updatedContent = new Content();
        updatedContent.setId(contentId);
        updatedContent.setTitle("Conteúdo Atualizado");

        ContentOutputDTO expectedOutput = new ContentOutputDTO();
        expectedOutput.setId(contentId);
        expectedOutput.setTitle("Conteúdo Atualizado");

        when(mapper.mapTo(updateContentDTO,Content.class)).thenReturn(updatedContent);
        when(contentRepository.findByIdAndSectionIdAndSection_Course_Id(contentId, sectionId, courseId))
                .thenReturn(Optional.of(existingContent));
        when(utils.getNullPropertyNames(any(Content.class))).thenReturn(new String[]{});
        when(contentRepository.save(existingContent)).thenReturn(updatedContent);
        when(mapper.mapTo(updatedContent, ContentOutputDTO.class)).thenReturn(expectedOutput);

        ContentOutputDTO result = contentService.update(1L, 1L, 1L, updateContentDTO);

        assertNotNull(result, "O resultado não deveria ser nulo");
        assertEquals(expectedOutput.getId(), result.getId(), "O ID do conteúdo retornado está incorreto");
        assertEquals(expectedOutput.getTitle(), result.getTitle(), "O título do conteúdo retornado está incorreto");

        verify(contentRepository).findByIdAndSectionIdAndSection_Course_Id(contentId, sectionId, courseId);
        verify(utils).getNullPropertyNames(any(Content.class));
        verify(contentRepository).save(existingContent);
        verify(mapper).mapTo(updatedContent, ContentOutputDTO.class);


    }

    @Test
    void deleteById() {
    }
}
