package ua.nure.readict.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.nure.readict.dto.LoginRequest;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.service.impl.AuthService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;


    @Test
    @DisplayName("Logout should return 204 No Content status")
    void logoutShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = authController.logout(request, this.response);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("Register should return 201 Created status")
    void registerShouldReturnCreatedStatus() {
        // Arrange
        UserDto userDto = new UserDto(
                "new.user@example.com",
                "Jane",
                "Smith",
                "password123",
                Set.of(1L, 3L)
        );

        doNothing().when(authService).register(userDto);

        // Act
        ResponseEntity<Void> response = authController.register(userDto);

        // Assert
        verify(authService).register(userDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
    }
}