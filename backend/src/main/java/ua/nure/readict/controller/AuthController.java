package ua.nure.readict.controller;

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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest dto,
                                      HttpServletRequest request) {
        authService.login(dto);
        request.getSession(true);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res) {
        authService.logout(req, res);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserDto dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/session")
    public ResponseEntity<SessionUserDto> getSessionUser(@AuthenticationPrincipal CurrentUser cu) {
        if (cu == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        User u = cu.getUser();
        return ResponseEntity.ok(new SessionUserDto(u.getId(), u.getFirstName(), u.getEmail(), u.getRole().getName()));
    }

}
