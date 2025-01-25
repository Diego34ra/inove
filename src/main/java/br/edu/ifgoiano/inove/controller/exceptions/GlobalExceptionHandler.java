package br.edu.ifgoiano.inove.controller.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletRequest request;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> notFoundException(ResourceNotFoundException ex, WebRequest webRequest){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorDetails errorDetails = new ErrorDetails(new Date(),status.value(), ex.getMessage(), getRequestPath());
        return ResponseEntity.status(status).body(errorDetails);
    }
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> notFoundException(SQLIntegrityConstraintViolationException ex, WebRequest webRequest){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorDetails errorDetails = new ErrorDetails(new Date(),status.value(), ex.getMessage(), getRequestPath());
        return ResponseEntity.status(status).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Dados inválidos");
    }

    @ExceptionHandler(ResourceBadRequestException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentExceptions(ResourceBadRequestException ex, WebRequest webRequest) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorDetails errorDetails = new ErrorDetails(new Date(),status.value(), ex.getMessage(), getRequestPath());
        return ResponseEntity.status(status).body(errorDetails);
    }

    public String getRequestPath(){
        return request.getRequestURI();
    }
}
