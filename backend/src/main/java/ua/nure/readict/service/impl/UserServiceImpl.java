package ua.nure.readict.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.entity.Genre;
import ua.nure.readict.entity.User;
import ua.nure.readict.event.FavouriteGenresChangedEvent;
import ua.nure.readict.mapper.UserMapper;
import ua.nure.readict.repository.GenreRepository;
import ua.nure.readict.repository.UserRepository;
import ua.nure.readict.service.interfaces.UserService;
import ua.nure.readict.util.Constants;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void updateFavouriteGenres(Long userId, Set<Long> genreIds) {
        User user = getUserOrThrow(userId);
        Set<Genre> genres = getGenresByIdsOrThrow(genreIds);

        user.getFavouriteGenres().clear();
        user.getFavouriteGenres().addAll(genres);
        userRepository.save(user);

        eventPublisher.publishEvent(new FavouriteGenresChangedEvent(userId));
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.toDto(getUserOrThrow(id));
    }

    public User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Constants.USER_NOT_FOUND.formatted(id)));
    }

    @Override
    @Transactional
    public UserDto updateUserNamesAndFavouriteGenres(Long userId, String firstName, String lastName, Set<Long> genreIds) {
        User user = getUserOrThrow(userId);

        user.setFirstName(Optional.ofNullable(firstName).map(String::trim).orElse(""));
        user.setLastName(Optional.ofNullable(lastName).map(String::trim).orElse(""));

        boolean genresChanged = false;

        if (genreIds != null) {
            Set<Long> currentIds = user.getFavouriteGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Genre> newGenres = getGenresByIdsOrThrow(genreIds);
            Set<Long> newIds = newGenres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            if (!currentIds.equals(newIds)) {
                user.setFavouriteGenres(newGenres);
                genresChanged = true;
            }
        }

        User savedUser = userRepository.save(user);

        if (genresChanged) {
            eventPublisher.publishEvent(new FavouriteGenresChangedEvent(user.getId()));
        }

        return userMapper.toDto(savedUser);
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserOrThrow(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private Set<Genre> getGenresByIdsOrThrow(Set<Long> genreIds) {
        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));

        if (genres.size() != genreIds.size()) {
            Set<Long> foundIds = genres.stream().map(Genre::getId).collect(Collectors.toSet());
            genreIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .ifPresent(missingId -> {
                        throw new EntityNotFoundException(Constants.GENRE_NOT_FOUND.formatted(missingId));
                    });
        }

        return genres;
    }
}
