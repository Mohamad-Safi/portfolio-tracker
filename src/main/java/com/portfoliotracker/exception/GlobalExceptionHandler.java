package com.portfoliotracker.exception;

import com.portfoliotracker.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UsernameTakenException.class, EmailTakenException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleConflict(RuntimeException ex) {
        return ErrorResponseDto.of(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleInvalidCredentials(InvalidCredentialsException ex) {
        return ErrorResponseDto.of(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            // a field can fail several rules; keep the first message
            fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ErrorResponseDto("Validation failed", fields);
    }
}
