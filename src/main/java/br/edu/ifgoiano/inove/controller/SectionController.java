package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.request.section.SectionRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.section.SectionSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ErrorDetails;
import br.edu.ifgoiano.inove.domain.model.Section;
import br.edu.ifgoiano.inove.domain.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inove/cursos/{courseId}/secoes")
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @GetMapping
    @Operation(summary = "Listar seções de um curso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seções listadas com sucesso.",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SectionSimpleResponseDTO.class)))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public List<SectionSimpleResponseDTO> list(@PathVariable Long courseId){
        return sectionService.list(courseId);
    }

    @GetMapping("/{sectionId}")
    @Operation(summary = "Buscar uma seção")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seção encontrada com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SectionSimpleResponseDTO.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> findOne(@PathVariable Long courseId, @PathVariable Long sectionId){
        return ResponseEntity.status(HttpStatus.OK).body(sectionService.findOne(courseId, sectionId));
    }

    @PostMapping
    @Operation(summary = "Cria uma seção")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Secao adicionado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Section.class))}),
            @ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public SectionResponseDTO create(@PathVariable Long courseId, @RequestBody @Validated SectionRequestDTO sectionDTO){
        return sectionService.create(courseId, sectionDTO);
    }

    @PutMapping("/{sectionId}")
    @Operation(summary = "Atualiza uma seção")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Secao atualizado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Section.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> update(@PathVariable Long courseId, @PathVariable Long sectionId, @RequestBody SectionRequestDTO sectionDTO){
        SectionResponseDTO updatedSection = sectionService.update(courseId, sectionId, sectionDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedSection);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{sectionId}")
    @Operation(summary = "Remove uma seção")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Secao deletado com sucesso."),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> delete(@PathVariable Long courseId,@PathVariable Long sectionId){
            sectionService.deleteById(courseId, sectionId);
            return ResponseEntity.noContent().build();
    }
}
