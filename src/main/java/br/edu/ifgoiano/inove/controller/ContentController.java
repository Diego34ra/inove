package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.request.content.ContentRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentOutputDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.ContentSimpleOutputDTO;
import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos")
public class ContentController {
    @Autowired
    private ContentService contentService;

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
    public ResponseEntity<?> findOne(@PathVariable Long sectionId, @PathVariable Long contentId){
        ContentOutputDTO savedContent = contentService.findOneById(sectionId, contentId);

        return ResponseEntity.status(HttpStatus.OK).body(savedContent);
    }

//    @PostMapping
//    @Operation(summary = "Adiciona um conteudo")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Coteudo adicionado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = Content.class))}),
//            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
//    })
//    public ContentOutputDTO create(@PathVariable Long courseId,
//                          @PathVariable Long sectionId,
//                          @RequestBody @Validated ContentInputDTO newContent){
//        return contentService.create(courseId, sectionId, newContent);
//    }

    @PutMapping("/{contentId}")
    @Operation(summary = "Atualiza um conteudo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coteudo atualizado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = Content.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> update(@PathVariable Long courseId,
                                    @PathVariable Long sectionId,
                                    @PathVariable Long contentId,
                                    @RequestBody ContentRequestDTO newContent){
        ContentOutputDTO updatedContent = contentService.update(courseId, sectionId, contentId, newContent);

        return ResponseEntity.status(HttpStatus.OK).body(updatedContent);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{contentId}")
    @Operation(summary = "Remove um conteudo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conteudo deletado com sucesso."),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> delete(@PathVariable Long sectionId,@PathVariable Long contentId){
            contentService.deleteById(sectionId, contentId);
            return ResponseEntity.noContent().build();
    }
}
