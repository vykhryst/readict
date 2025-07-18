package ua.nure.readict.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email адреса не може бути порожньою")
        @Email(message = "Email адреса повинна бути дійсною")
        @Size(max = 255, message = "Довжина email адреси повинна бути не більше 255 символів")
        String email,

        @NotBlank(message = "Пароль не може бути порожнім")
        @Size(min = 8, max = 255, message = "Пароль повинен містити від 8 до 255 символів")
        String password
) {
}
