package br.edu.ifgoiano.inove.controller;

import br.edu.ifgoiano.inove.controller.dto.mapper.MyModelMapper;
import br.edu.ifgoiano.inove.controller.dto.request.auth.AuthenticationDTO;
import br.edu.ifgoiano.inove.controller.dto.request.auth.PasswordResetDTO;
import br.edu.ifgoiano.inove.controller.dto.request.auth.RecoveryCodeDTO;
import br.edu.ifgoiano.inove.controller.dto.request.user.UserRequestDTO;
import br.edu.ifgoiano.inove.controller.dto.response.login.LoginResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.request.auth.RefreshTokenDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserDetailResponseDTO;
import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.controller.exceptions.ErrorDetails;
import br.edu.ifgoiano.inove.controller.exceptions.ResourceBadRequestException;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.service.PasswordRecoveryService;
import br.edu.ifgoiano.inove.domain.service.SchoolService;
import br.edu.ifgoiano.inove.domain.service.UserService;
import br.edu.ifgoiano.inove.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/inove/auth")
@Tag(name = "Auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private MyModelMapper mapper;

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @PostMapping("login")
    @Operation(summary = "Realizar autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Erro ao autenticar usuário.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO authenticationDTO){
        System.out.println("Login autenticado");
        var userNamePassword = new UsernamePasswordAuthenticationToken(authenticationDTO.email(),authenticationDTO.password());
        System.out.println("Login autenticado 2");
        var auth = authenticationManager.authenticate(userNamePassword);
        System.out.println("Login autenticado 3");
        var loginResponse = tokenService.getAuthentication((User) auth.getPrincipal());
        System.out.println("Login autenticado 4");
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("refresh-token")
    @Operation(summary = "Atualizar autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticação atualizada com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RefreshTokenDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar autenticação.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO){
        var loginResponse = tokenService.getRefreshToken(refreshTokenDTO);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("register")
    @Operation(summary = "Criar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Erro ao registrar usuário.",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))})
    })
    public ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO user){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(user));
    }

    @PostMapping("forgot-password/{email}")
    @Operation(summary = "Envia email com codigo de verificação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Se usuário relacionado ao email existe recebera um email com código de verificação"),
    })
    public ResponseEntity<Void> requestRecoveryCode(@PathVariable("email")
                                                        @NotBlank(message = "O e-mail é obrigatório.")
                                                        @Email(message = "O e-mail deve ser válido.")
                                                        String email){
        passwordRecoveryService.requestRecoveryCode(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("verify-code")
    @Operation(summary = "Valida código de verificação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Código inválido ou expirado."),
    })
    public ResponseEntity<Void> verifyRecoveryCode(@Valid @RequestBody RecoveryCodeDTO dto) {
        boolean valid = passwordRecoveryService.validateCode(dto.getEmail(), dto.getCode());
        if (!valid) {
            throw new ResourceBadRequestException("Código inválido ou expirado.");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("reset-password")
    @Operation(summary = "Altera a senha com código de verificação valido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso."),
    })
    public ResponseEntity<UserResponseDTO> resetPassword(@Valid @RequestBody PasswordResetDTO dto){
        boolean valid = passwordRecoveryService.consumeCode(dto.getEmail(), dto.getCode());
        if (!valid) {
            throw new ResourceBadRequestException("Código inválido ou expirado.");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.mapTo(
                        userService.updatePasswordByEmail(dto.getEmail(),
                        dto.getCode()), UserResponseDTO.class));
    }

    @PostMapping("teste")
    public ResponseEntity<Void> teste(){
        String rawPassword = "asdasd2";
        String hashedPassword = "$2a$10$P9v0JFoS7fA45KI/ULG9HuP6g2M2KGm20aBVNw.zT6/G0qsInWqF6";// resultado do create
        String hashedPassword2 = "$2a$10$qJyIi4IKEIe4NbpyPhSTxO33EO2TemgmjnSFnqlcG6jmKK5axLKTO";// resultado do update passworld

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        boolean matches = encoder.matches(rawPassword, hashedPassword);
        boolean matches2 = encoder.matches(rawPassword, hashedPassword2);
        System.out.println("Senha confere? " + matches);// true
        System.out.println("Senha confere 2? " + matches2);// false
        return ResponseEntity.ok().build();
    }

}
