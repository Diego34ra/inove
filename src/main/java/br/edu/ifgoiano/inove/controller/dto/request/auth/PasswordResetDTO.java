package br.edu.ifgoiano.inove.controller.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO extends RecoveryCodeDTO{

    @NotBlank(message = "A senha é obrigatória.")
    private String password;

}
