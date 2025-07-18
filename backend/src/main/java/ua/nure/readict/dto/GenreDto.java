package ua.nure.readict.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * DTO for {@link ua.nure.readict.entity.Genre}
 */
@Schema(description = "DTO for genre details")
public record GenreDto(

        @Schema(description = "ID of the genre", example = "1")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @Schema(description = "Name of the genre", example = "Fantasy")
        String name
) implements Serializable {
}
