package br.com.fiap.ecoactions_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        var details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Erro de validação nos dados enviados.", request.getRequestURI(), details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno na aplicação.", request.getRequestURI(), List.of());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            String path,
            List<String> details
    ) {
        var response = new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                traduzirStatus(status),
                message,
                path,
                details
        );

        return ResponseEntity.status(status).body(response);
    }

    private String traduzirStatus(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Requisição inválida";
            case NOT_FOUND -> "Não encontrado";
            case INTERNAL_SERVER_ERROR -> "Erro interno do servidor";
            default -> status.getReasonPhrase();
        };
    }
}
