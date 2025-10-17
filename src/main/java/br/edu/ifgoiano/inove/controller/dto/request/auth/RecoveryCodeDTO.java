package br.edu.ifgoiano.inove.controller.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoveryCodeDTO {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    private String email;

    @NotBlank(message = "O código é obrigatório.")
    @Pattern(regexp = "\\d{6}", message = "O código deve conter exatamente 6 dígitos numéricos.")
    private String code;
}
