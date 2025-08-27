package ua.nure.readict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "API for managing user profile and preferences")
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/favouriteGenres")
    @Operation(summary = "Update favourite genres for a user", description = "Updates the set of favourite genres for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Favourite genres updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void updateFavouriteGenres(
            @Parameter(description = "ID of the user") @PathVariable Long id,
            @Parameter(description = "Set of genre IDs") @RequestBody @Valid Set<Long> genreIds) {

        userService.updateFavouriteGenres(id, genreIds);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info", description = "Returns the currently authenticated user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User info retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })
    public UserDto getCurrentUser(@AuthenticationPrincipal CurrentUser cu) {
        return userService.getById(cu.getUser().getId());
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user info", description = "Updates the first name, last name, and favourite genres of the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User info updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user update data")
    })
    public ResponseEntity<UserDto> updateCurrentUser(
            @Parameter(description = "Updated user data") @RequestBody @Valid UserUpdateDto dto,
            @AuthenticationPrincipal CurrentUser cu) {

        UserDto updatedUser = userService.updateUserNamesAndFavouriteGenres(
                cu.getUser().getId(),
                dto.firstName(),
                dto.lastName(),
                dto.favouriteGenreIds());

        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password", description = "Changes the password of the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Current password is incorrect or new password is invalid")
    })
    public ResponseEntity<?> changePassword(
            @Parameter(description = "Password update request") @RequestBody @Valid PasswordDto dto,
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
