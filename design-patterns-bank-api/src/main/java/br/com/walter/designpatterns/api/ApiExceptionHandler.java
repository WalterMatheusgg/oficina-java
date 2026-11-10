package br.com.walter.designpatterns.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleInvalidRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiError(exception.getMessage()));
    }
}
