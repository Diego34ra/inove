package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.request.course.CourseRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.content.completedContent.CompletedContentResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.course.CourseSimpleResponseDTO;
import br.edu.ifgoiano.inove.domain.service.CourseService;
import br.edu.ifgoiano.inove.domain.service.UserCompletedContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inove/cursos")
public class CourseController {

    @Autowired
    private CourseService cursoService;

    @Autowired
    private UserCompletedContentService completedContentService;

    @PostMapping
    @Operation(summary = "Cria um curso")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Coteudo adicionado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO> create(@RequestBody CourseRequestDTO curso){
        var cursoCreated = cursoService.create(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoCreated);
    }

    @GetMapping
    @Operation(summary = "Listar cursos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cursos listados com sucesso.",
                    content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CourseSimpleResponseDTO.class)))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<List<CourseSimpleResponseDTO>> list(){
        return ResponseEntity.status(HttpStatus.OK).body(cursoService.findAll());
    }

    @GetMapping("{id}")
    @Operation(summary = "Buscar um curso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coteudo encontrado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO> findOne(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(cursoService.findOneById(id));
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualiza um curso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coteudo atualizado com sucesso.",content = { @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @Schema(implementation = br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<br.edu.ifgoiano.inove.controller.dto.response.course.CourseResponseDTO> update(@PathVariable  Long id, @RequestBody CourseRequestDTO curso){
        return ResponseEntity.status(HttpStatus.OK).body(cursoService.update(id,curso));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Remove um curso")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Curso deletado com sucesso."),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> delete(@PathVariable  Long id){
        cursoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/instrutor/{instructorId}")
    @Operation(summary = "Listar cursos de um instrutor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cursos listados com sucesso.",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CourseSimpleResponseDTO.class))) }),
            @ApiResponse(responseCode = "401", description = "Acesso negado.", content = @Content)
    })
    public ResponseEntity<List<CourseSimpleResponseDTO>> listInstructorCourses(@PathVariable Long instructorId) {
        return ResponseEntity.status(HttpStatus.OK).body(cursoService.findCoursesByInstructor(instructorId));
    }

    @GetMapping("{courseId}/discente/{userId}/progresso")
    @Operation(summary = "Listar cursos de um instrutor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cursos listados com sucesso.",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CompletedContentResponseDTO.class)))}),
            @ApiResponse(responseCode = "401", description = "Acesso negado.", content = @Content)
    })
    public ResponseEntity<CompletedContentResponseDTO> getStudentProgress(@PathVariable Long courseId, @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(completedContentService.getUserProgress(courseId, userId));
    }

}
