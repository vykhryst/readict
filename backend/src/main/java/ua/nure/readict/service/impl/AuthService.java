package ua.nure.readict.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.nure.readict.dto.LoginRequest;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.entity.Genre;
import ua.nure.readict.entity.Role;
import ua.nure.readict.entity.User;
import ua.nure.readict.mapper.UserMapper;
import ua.nure.readict.repository.RoleRepository;
import ua.nure.readict.repository.UserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public void login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Transactional
    public void register(UserDto dto) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(dto.email()))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email %s is already in use".formatted(dto.email())
            );
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default USER role not found"));

        User user = userMapper.toEntity(dto);
        user.setRole(userRole);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        // Set favourite genres ids from DTO to Entity
        user.setFavouriteGenres(dto.favouriteGenreIds().stream()
                .map(Genre::new) // Створюємо Genre з id
                .collect(Collectors.toSet()));

        userRepository.save(user);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler()
                    .logout(request, response, auth);
        }
    }
}
