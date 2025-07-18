package ua.nure.readict.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * DTO for {@link ua.nure.readict.entity.Trope}
 */
@Schema(description = "DTO for trope details")
public record TropeDto(

        @Schema(description = "ID of the trope", example = "1")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @Schema(description = "Name of the trope", example = "Chosen One")
        String name

) implements Serializable {
}
