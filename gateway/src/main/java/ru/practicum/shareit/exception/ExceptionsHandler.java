package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExceptionsHandler {
    @ExceptionHandler
    public List<FieldError> fieldValidationExceptionHandler(MethodArgumentNotValidException exception) {
        log.error("Invalid arguments", exception);
        return exception
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler
    public FieldError constraintValidationExceptionHandler(ConstraintViolationException exception) {
        log.error("Invalid arguments", exception);
        return new FieldError("", exception.getMessage());
    }

    @ExceptionHandler
    public List<FieldError> fieldValidationExceptionHandler(FieldValidationException exception) {
        log.error("Invalid arguments", exception);
        return List.of(new FieldError(exception.getField(), exception.getDescription()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> internalServerErrorHandler(Exception exception) {
        log.error("Internal error", exception);
        return Map.of("error", exception.getMessage());
    }
}