package ua.nure.readict.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * DTO for {@link ua.nure.readict.entity.Series}
 */
@Schema(description = "DTO for series details")
public record SeriesDto(

        @Schema(description = "ID of the series", example = "1")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @Schema(description = "Name of the series", example = "The Chronicles of Narnia")
        String name

) implements Serializable {
}
