package ua.nure.readict.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.entity.Role;
import ua.nure.readict.entity.User;
import ua.nure.readict.mapper.UserMapper;
import ua.nure.readict.repository.RoleRepository;
import ua.nure.readict.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Register new user with unique email should create user")
    void registerNewUserWithUniqueEmail() {
        // Arrange
        String email = "new.user@example.com";
        String firstName = "Jane";
        String lastName = "Smith";
        String password = "password123";
        String encodedPassword = "encoded_password";
        Set<Long> favouriteGenreIds = Set.of(1L, 3L);

        UserDto userDto = new UserDto(
                email,
                firstName,
                lastName,
                password,
                favouriteGenreIds
        );

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFavouriteGenres(new HashSet<>());

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // Act
        authService.register(userDto);

        // Assert
        verify(userRepository).existsByEmail(email);
        verify(roleRepository).findByName("USER");
        verify(userMapper).toEntity(userDto);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getPasswordHash()).isEqualTo(encodedPassword);
        assertThat(savedUser.getRole()).isEqualTo(userRole);
    }

    @Test
    @DisplayName("Register user with existing email should throw exception")
    void registerUserWithExistingEmailShouldThrowException() {
        // Arrange
        String email = "existing.user@example.com";
        UserDto userDto = new UserDto(
                email,
                "Jane",
                "Smith",
                "password123",
                Set.of(1L, 3L)
        );

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(userDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT)
                .hasMessageContaining("Email already in use");

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Register user should throw exception if USER role not found")
    void registerUserShouldThrowExceptionIfUserRoleNotFound() {
        // Arrange
        String email = "new.user@example.com";
        UserDto userDto = new UserDto(
                email,
                "Jane",
                "Smith",
                "password123",
                Set.of(1L, 3L)
        );

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.register(userDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Default USER role not found");

        verify(userRepository).existsByEmail(email);
        verify(roleRepository).findByName("USER");
        verify(userRepository, never()).save(any(User.class));
    }
}