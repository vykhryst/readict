package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.readict.dto.LoginRequest;
import ua.nure.readict.dto.SessionUserDto;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.entity.User;
import ua.nure.readict.service.impl.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "API for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates the user with email and password and creates a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Incorrect email or password")
    })
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest dto,
                                      HttpServletRequest request) {
        authService.login(dto);
        request.getSession(true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Terminates the user session and logs out")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully logged out")
    })
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res) {
        authService.logout(req, res);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    public ResponseEntity<Void> register(@RequestBody @Valid UserDto dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/session")
    @Operation(summary = "Get current user info", description = "Returns information about the user from the current session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })
    public ResponseEntity<SessionUserDto> getSessionUser(@AuthenticationPrincipal CurrentUser cu) {
        if (cu == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        User u = cu.getUser();
        return ResponseEntity.ok(new SessionUserDto(u.getId(), u.getFirstName(), u.getEmail(), u.getRole().getName()));
    }
}
