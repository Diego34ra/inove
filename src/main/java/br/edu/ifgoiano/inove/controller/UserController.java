package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.request.user.*;
import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.response.user.StudentResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserSimpleResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ErrorDetails;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceNotFoundException;
import br.edu.ifgoiano.inove.domain.model.*;
import br.edu.ifgoiano.inove.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inove/usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MyModelMapper mapper;

    @GetMapping
    @Operation(summary = "Listar Usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios listados com sucesso.",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = User.class)))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public List<UserSimpleResponseDTO> listUsers(){
        return userService.list();
    }

    @GetMapping("/admin")
    @Operation(summary = "Listar Adiministradores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Adiministradores listados com sucesso.",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public List<UserResponseDTO> listAdmins(){
        return userService.listAdmins();
    }

    @GetMapping("/discente")
    @Operation(summary = "Listar discentes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Discentes listados com sucesso.",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StudentResponseDTO.class)))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public List<StudentResponseDTO> listStudents(){
        return userService.listStudents();
    }

    @GetMapping("/instrutor")
    @Operation(summary = "Listar instrutores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instrutores listados com sucesso.",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public List<UserResponseDTO> listInstructors(){
        return userService.listInstructors();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Buscar um usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            //@ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> findUser(@PathVariable Long userId){
        try {
            UserResponseDTO savedUser = userService.findOneById(userId);

            return ResponseEntity.status(HttpStatus.OK).body(savedUser);
        }catch(ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/admin")
    @Operation(summary = "Cria um Usuario Interno.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario adicionado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public UserResponseDTO createAdmin(@RequestBody @Valid UserRequestDTO admin){
        return mapper.mapTo(userService.create(admin), UserResponseDTO.class);
    }

    @PostMapping("/discente")
    @Operation(summary = "Cria um discente na platforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Discente adicionado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = StudentResponseDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public StudentResponseDTO createStudent(@RequestBody StudentRequestDTO studentDTO){
        return userService.create(studentDTO.getSchool().getId(), studentDTO);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Atualiza um usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user){
        UserResponseDTO updatedEscola = userService.update(userId, user);

        return ResponseEntity.status(HttpStatus.OK).body(updatedEscola);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    @Operation(summary = "Remove um curso")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Curso deletado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Acesso negado.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<?> deleteUser(@PathVariable String userId){
            userService.deleteById(Long.parseLong(userId));
            return ResponseEntity.noContent().build();
    }
}
