package ua.nure.readict.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.nure.readict.dto.PasswordDto;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.dto.UserUpdateDto;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.entity.User;
import ua.nure.readict.mapper.UserMapper;
import ua.nure.readict.service.interfaces.UserService;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    private void setupSecurityContext(User user) {
        CurrentUser currentUser = new CurrentUser(user, null);
        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Get current user information")
    void getCurrentUserInfo() {
        // Arrange
        User user = createTestUser();

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            CurrentUser currentUser = new CurrentUser(user, null);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(currentUser);

            UserDto userDto = new UserDto(
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    null, // password is not returned
                    Set.of(1L, 2L)
            );

            when(userService.getById(user.getId())).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userDto);

            // Act
            UserDto result = userController.getMe();

            // Assert
            verify(userService).getById(user.getId());
            verify(userMapper).toDto(user);

            assertThat(result).isEqualTo(userDto);
        }
    }

    @Test
    @DisplayName("Update user profile information")
    void updateUserProfile() {
        // Arrange
        User user = createTestUser();
        String newFirstName = "UpdatedFirstName";
        String newLastName = "UpdatedLastName";
        Set<Long> newFavouriteGenreIds = Set.of(3L, 4L);

        UserUpdateDto updateDto = new UserUpdateDto(
                newFirstName,
                newLastName,
                newFavouriteGenreIds
        );

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            CurrentUser currentUser = new CurrentUser(user, null);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(currentUser);

            User updatedUser = createTestUser();
            updatedUser.setFirstName(newFirstName);
            updatedUser.setLastName(newLastName);

            UserDto updatedUserDto = new UserDto(
                    updatedUser.getEmail(),
                    updatedUser.getFirstName(),
                    updatedUser.getLastName(),
                    null, // password is not returned
                    newFavouriteGenreIds
            );

            doNothing().when(userService).updateNamesAndGenres(
                    eq(user),
                    eq(newFirstName),
                    eq(newLastName),
                    eq(newFavouriteGenreIds)
            );

            when(userService.getById(user.getId())).thenReturn(updatedUser);
            when(userMapper.toDto(updatedUser)).thenReturn(updatedUserDto);

            // Act
            ResponseEntity<UserDto> response = userController.updateMe(updateDto);

            // Assert
            verify(userService).updateNamesAndGenres(
                    eq(user),
                    eq(newFirstName),
                    eq(newLastName),
                    eq(newFavouriteGenreIds)
            );
            verify(userService).getById(user.getId());
            verify(userMapper).toDto(updatedUser);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(updatedUserDto);
        }
    }

    @Test
    @DisplayName("Change password successfully")
    void changePasswordSuccessfully() {
        // Arrange
        User user = createTestUser();
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        PasswordDto passwordDto = new PasswordDto(currentPassword, newPassword);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            CurrentUser currentUser = new CurrentUser(user, null);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(currentUser);

            doNothing().when(userService).changePassword(
                    eq(user.getId()),
                    eq(currentPassword),
                    eq(newPassword)
            );

            // Act
            ResponseEntity<?> response = userController.changePassword(passwordDto);

            // Assert
            verify(userService).changePassword(
                    eq(user.getId()),
                    eq(currentPassword),
                    eq(newPassword)
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
        }
    }

    @Test
    @DisplayName("Change password with incorrect current password")
    void changePasswordWithIncorrectCurrentPassword() {
        // Arrange
        User user = createTestUser();
        String incorrectCurrentPassword = "wrongPassword";
        String newPassword = "newPassword";

        PasswordDto passwordDto = new PasswordDto(incorrectCurrentPassword, newPassword);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            CurrentUser currentUser = new CurrentUser(user, null);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(currentUser);

            doThrow(new IllegalArgumentException("Current password is incorrect"))
                    .when(userService)
                    .changePassword(
                            eq(user.getId()),
                            eq(incorrectCurrentPassword),
                            eq(newPassword)
                    );

            // Act
            ResponseEntity<?> response = userController.changePassword(passwordDto);

            // Assert
            verify(userService).changePassword(
                    eq(user.getId()),
                    eq(incorrectCurrentPassword),
                    eq(newPassword)
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            Map<String, String> responseBody = (Map<String, String>) response.getBody();
            assertThat(responseBody).containsEntry("error", "Current password is incorrect");
        }
    }

    @Test
    @DisplayName("Update user favourite genres")
    void updateUserFavouriteGenres() {
        // Arrange
        Long userId = 1L;
        Set<Long> genreIds = Set.of(1L, 3L, 5L);

        doNothing().when(userService).updateFavouriteGenres(userId, genreIds);

        // Act
        userController.updateFavouriteGenres(userId, genreIds);

        // Assert
        verify(userService).updateFavouriteGenres(userId, genreIds);
    }
}