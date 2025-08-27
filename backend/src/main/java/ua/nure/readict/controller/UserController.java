package ua.nure.readict.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.PasswordDto;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.dto.UserUpdateDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.service.interfaces.UserService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/favouriteGenres")
    public void updateFavouriteGenres(@PathVariable Long id, @RequestBody @Valid Set<Long> genreIds) {
        userService.updateFavouriteGenres(id, genreIds);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal CurrentUser cu) {
        return userService.getById(cu.getUser().getId());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody @Valid UserUpdateDto dto,
                                                     @AuthenticationPrincipal CurrentUser cu) {
        UserDto updatedUser = userService.updateUserNamesAndFavouriteGenres(
                cu.getUser().getId(),
                dto.firstName(),
                dto.lastName(),
                dto.favouriteGenreIds());

        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordDto dto,
                                            @AuthenticationPrincipal CurrentUser cu) {
        try {
            userService.changePassword(
                    cu.getUser().getId(),
                    dto.currentPassword(),
                    dto.newPassword());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

}
