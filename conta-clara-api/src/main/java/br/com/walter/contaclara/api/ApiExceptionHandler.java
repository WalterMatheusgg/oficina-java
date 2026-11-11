package br.com.walter.contaclara.api;

import br.com.walter.contaclara.application.FinanceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(br.com.walter.contaclara.ai.VoiceAssistantService.AssistantProviderException.class)
    public ResponseEntity<ApiError> assistantUnavailable(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiError.of(502, "Bad Gateway", exception.getMessage()));
    }

    @ExceptionHandler(FinanceNotFoundException.class)
    public ResponseEntity<ApiError> notFound(FinanceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, "Not Found", exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
            ConstraintViolationException.class})
    public ResponseEntity<ApiError> badRequest(Exception exception) {
        String message = exception instanceof HttpMessageNotReadableException
                ? "Confira os campos e os formatos enviados."
                : exception.getMessage();
        return ResponseEntity.badRequest().body(ApiError.of(400, "Bad Request", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() == null ? "Valor inválido." : error.getDefaultMessage(),
                        (first, ignored) -> first));
        return ResponseEntity.badRequest().body(new ApiError(
                java.time.Instant.now(), 400, "Bad Request",
                "Há campos ausentes ou inválidos.", fields));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> fileTooLarge(MaxUploadSizeExceededException exception) {
        return ResponseEntity.badRequest()
                .body(ApiError.of(400, "Bad Request", "O áudio deve ter no máximo 15 MB."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> dataConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, "Conflict", "A operação conflitou com um dado já existente."));
    }
}
