package br.edu.ifgoiano.inove.controller.dto.response.login;

public record LoginResponseDTO (String token, String refreshToken, Long userId){
}
