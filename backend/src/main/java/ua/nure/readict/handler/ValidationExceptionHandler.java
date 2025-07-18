package ua.nure.readict.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String expectedType = Optional.ofNullable(ex.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("Unknown");
        String errorMessage = String.format("Invalid value. Expected type: %s", expectedType);
        Map<String, String> errors = Map.of(parameterName, errorMessage);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String parameterName = ex.getParameterName();
        String errorMessage = "Missing required parameter";
        Map<String, String> errors = Map.of(parameterName, errorMessage);
        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Invalid value"),
                        (existing, replacement) -> existing
                ));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = ex.getAllErrors().stream()
                .collect(Collectors.toMap(
                        this::resolveFieldName,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Invalid value"),
                        (existing, replacement) -> existing
                ));
        return ResponseEntity.badRequest().body(errors);
    }

    private String resolveFieldName(Object error) {
        if (error instanceof FieldError fieldError) {
            return fieldError.getField();
        }
        return "Unknown field";
    }
}
