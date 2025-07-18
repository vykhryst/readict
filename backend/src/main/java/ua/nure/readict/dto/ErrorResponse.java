package ua.nure.readict.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Виключити null-поля з відповіді
public class ErrorResponse {

    @Schema(description = "Error", example = "Internal server error")
    private String error;

    @Schema(description = "Message", example = "Something went wrong")
    private String message;

    @Schema(description = "Status", example = "500")
    private Integer status;

    @Schema(description = "Path", example = "/api/v1/books")
    private String path;

    @Schema(description = "Timestamp", example = "2021-10-01T12:00:00")
    private LocalDateTime timestamp;
}
