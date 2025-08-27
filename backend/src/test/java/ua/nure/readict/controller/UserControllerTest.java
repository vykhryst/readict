package ua.nure.readict.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.nure.readict.entity.CurrentUser;
import ua.nure.readict.entity.User;
import ua.nure.readict.mapper.UserMapper;
import ua.nure.readict.service.interfaces.UserService;

import java.util.Set;

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