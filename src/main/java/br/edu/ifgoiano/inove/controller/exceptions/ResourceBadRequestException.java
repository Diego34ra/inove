package br.edu.ifgoiano.inove.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceBadRequestException extends IllegalArgumentException{

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceBadRequestException(String message) {
        super(message);
    }
}
