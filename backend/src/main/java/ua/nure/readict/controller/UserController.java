package ua.nure.readict.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ua.nure.readict.dto.PasswordDto;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.dto.UserUpdateDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.mapper.UserMapper;
import ua.nure.readict.service.interfaces.UserService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @PutMapping("/{id}/favouriteGenres")
    public void updateFavouriteGenres(@PathVariable Long id, @RequestBody @Valid Set<Long> genreIds) {
        userService.updateFavouriteGenres(id, genreIds);
    }


    @GetMapping("/me")
    public UserDto getMe() {
        return userMapper.toDto(
                userService.getById(getCurrentUser().getUser().getId()));
    }

    private CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMe(@RequestBody @Valid UserUpdateDto dto) {

        userService.updateNamesAndGenres(
                getCurrentUser().getUser(),
                dto.firstName(),
                dto.lastName(),
                dto.favouriteGenreIds());

        UserDto fresh = userMapper.toDto(
                userService.getById(getCurrentUser().getUser().getId()));
        return ResponseEntity.ok(fresh);
    }


    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordDto dto) {
        try {
            userService.changePassword(
                    getCurrentUser().getUser().getId(),
                    dto.currentPassword(),
                    dto.newPassword());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

}
