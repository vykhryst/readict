package ua.nure.readict.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * DTO for {@link ua.nure.readict.entity.Author}
 */
@Schema(description = "DTO for author details")
public record AuthorDto(

        @Schema(description = "ID of the author", example = "1")
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @Schema(description = "First name of the author", example = "George")
        String firstName,

        @Schema(description = "Middle name of the author (optional)", example = "R.R.")
        String middleName,

        @Schema(description = "Last name of the author", example = "Martin")
        String lastName,

        @Schema(description = "Biography of the author", example = "Biography of George R.R. Martin...")
        String biography

) implements Serializable {
}
