package ua.nure.readict.handler;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ua.nure.readict.dto.ErrorResponse;
import ua.nure.readict.exception.FieldNotUniqueException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return buildErrorResponse(
                ex,
                HttpStatus.NOT_FOUND,
                "Entity not found",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        return buildErrorResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "Data integrity violation",
                request
        );
    }

    @ExceptionHandler(FieldNotUniqueException.class)
    public ResponseEntity<ErrorResponse> handleNameAlreadyExistsException(FieldNotUniqueException ex, WebRequest request) {
        return buildErrorResponse(
                ex,
                HttpStatus.BAD_REQUEST,
                "Field not unique",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, String error, WebRequest request) {
        ErrorResponse response = new ErrorResponse(
                error,
                ex.getMessage(),
                status.value(),
                request.getDescription(false).replace("uri=", ""), // Отримати URL без префікса `uri=`
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(response);
    }
}
