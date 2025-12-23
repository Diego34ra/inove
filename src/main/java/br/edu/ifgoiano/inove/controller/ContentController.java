package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.request.content.ContentRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.request.content.ContentSimpleRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentSimpleOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.completedContent.CompletedContentMinDTO;
import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.model.ContentType;
import br.edu.ifgoiano.inove.domain.model.UserCompletedContent;
import br.edu.ifgoiano.inove.domain.repository.UserCompletedContentRepository;
import br.edu.ifgoiano.inove.domain.service.ContentService;
import br.edu.ifgoiano.inove.domain.service.FileService;
import br.edu.ifgoiano.inove.domain.service.UserCompletedContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos")
public class ContentController {
    @Autowired
    private ContentService contentService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserCompletedContentService completedContentService;
    @Autowired
    private UserCompletedContentRepository userCompletedContentRepository;


    @PostMapping("/upload")
    public ResponseEntity<?> createContentWithFile(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("contentType") ContentType contentType) {

        try {
            ContentSimpleRequestDTO contentDTO = new ContentSimpleRequestDTO(title, description, contentType, null, null);
            String uploadMessage = fileService.upload(file, courseId, sectionId, contentDTO);

            Map<String, String> response = new HashMap<>();
            response.put("message", uploadMessage);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao processar o arquivo.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    @Operation(summary = "Listar conteudos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coteudos listados com sucesso.",
                    content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ContentSimpleOutputDTO.class)))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public List<ContentSimpleOutputDTO> list(@PathVariable Long sectionId){
        return contentService.list(sectionId);
    }

    @GetMapping("/{contentId}")
    @Operation(summary = "Buscar um conteudo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coteudo encontrado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = ContentOutputDTO.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> findOne(@PathVariable Long courseId,
                                     @PathVariable Long sectionId,
                                     @PathVariable Long contentId){
        ContentOutputDTO savedContent = contentService.findOneById(courseId, sectionId, contentId);

        return ResponseEntity.status(HttpStatus.OK).body(savedContent);
    }

    @PostMapping
    @Operation(summary = "Adiciona um conteudo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Coteudo adicionado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = Content.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ContentOutputDTO create(@PathVariable Long courseId,
                          @PathVariable Long sectionId,
                          @RequestBody @Validated ContentRequestDTO newContent){
        return contentService.create(courseId, sectionId, newContent);
    }

    @PutMapping("/{contentId}")
    @Operation(summary = "Atualiza um conteudo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coteudo atualizado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = Content.class))}),
    })
    public ResponseEntity<?> update(@PathVariable Long courseId,
                                    @PathVariable Long sectionId,
                                    @PathVariable Long contentId,
                                    @RequestBody ContentRequestDTO newContent){
        ContentOutputDTO updatedContent = contentService.update(courseId, sectionId, contentId, newContent);

        return ResponseEntity.status(HttpStatus.OK).body(updatedContent);
    }

    @PutMapping("/{contentId}/upload")
    @Transactional
    @Operation(summary = "Atualiza um conteudo com novo arquivo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteudo e arquivo atualizados com sucesso."),
    })
    public ResponseEntity<?> updateContentWithFile(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long contentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("contentType") ContentType contentType) {

        try {
            ContentSimpleRequestDTO contentDTO = new ContentSimpleRequestDTO(title, description, contentType, null, null);

            userCompletedContentRepository.deleteByCourseId(courseId);
            userCompletedContentRepository.flush();

            String uploadMessage = fileService.updateContentFile(file, courseId, sectionId, contentId, contentDTO);

            Map<String, String> response = new HashMap<>();
            response.put("message", uploadMessage);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao processar o arquivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{contentId}")
    @Operation(summary = "Remove um conteudo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conteudo deletado com sucesso."),
    })
    public ResponseEntity<?> delete(@PathVariable Long courseId,
                                    @PathVariable Long sectionId,
                                    @PathVariable Long contentId){
            fileService.delete(courseId, sectionId, contentId);
            return ResponseEntity.noContent().build();
    }


    @PostMapping("/{contentId}/discente/{userId}/progresso")
    @Operation(summary = "Salva Conteúdo como Concluido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Marcar conteúdo como concluído.",
                    content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @Schema(implementation = Content.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",
            // content = { @Content(mediaType = "application/json",
            // schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<CompletedContentMinDTO> saveUserProgress(@PathVariable("courseId") Long courseId,
                                                                   @PathVariable("sectionId") Long sectionId,
                                                                   @PathVariable("contentId") Long contentId,
                                                                   @PathVariable("userId") Long userId){
        return ResponseEntity.ok().body(completedContentService.create(courseId, sectionId, contentId, userId));
    }
}
