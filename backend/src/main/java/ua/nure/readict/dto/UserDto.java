package ua.nure.readict.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;


public record UserDto(
        @NotBlank(message = "Email адреса не може бути порожньою")
        @Email(message = "Email адреса повинна бути дійсною")
        @Size(max = 255, message = "Довжина email адреси повинна бути не більше 255 символів")
        String email,

        @NotBlank(message = "Ім'я користувача не може бути порожнім")
        @Size(max = 255, message = "Ім'я користувача повинно містити до 255 символів")
        String firstName,

        @NotBlank(message = "Прізвище користувача не може бути порожнім")
        @Size(max = 255, message = "Прізвище користувача повинно містити до 255 символів")
        String lastName,

        @NotBlank(message = "Пароль не може бути порожнім")
        @Size(min = 8, max = 255, message = "Пароль повинен містити від 8 до 255 символів")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        @NotNull(message = "Виберіть хоча б один жанр")
        @Size(min = 1, message = "Виберіть хоча б один жанр")
        Set<Long> favouriteGenreIds

) {
}