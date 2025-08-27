package ua.nure.readict.dto;

public record SessionUserDto(
        Long id,
        String firstName,
        String email,
        String role
) {
}
