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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final ApplicationEventPublisher events;
    private final UserMapper userMapper;
    private final PasswordEncoder pwdEncoder;


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void updateFavouriteGenres(Long userId, Set<Long> genreIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.getFavouriteGenres().clear();
        for (Long id : genreIds) {
            Genre genre = genreRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
            user.getFavouriteGenres().add(genre);
        }
        userRepository.save(user);
        events.publishEvent(new FavouriteGenresChangedEvent(userId));
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void updateNamesAndGenres(User user,
                                     String first,
                                     String last,
                                     Set<Long> genreIds) {
        user.setFirstName(first.trim());
        user.setLastName(last.trim());
        boolean genresChanged = false;                          // ← прапорець

        if (genreIds != null) {
            Set<Long> oldIds = user.getFavouriteGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            Set<Genre> newGenres = new HashSet<>(genreRepository.findAllById(genreIds));
            user.setFavouriteGenres(newGenres);

            Set<Long> newIds = newGenres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            genresChanged = !oldIds.equals(newIds);             // ← перевірка
        }

        userRepository.save(user);

        if (genresChanged) {
            events.publishEvent(new FavouriteGenresChangedEvent(user.getId()));
        }
    }


    @Override
    public void changePassword(Long id, String currentPwd, String newPwd) {

        User u = getById(id);
        if (!pwdEncoder.matches(currentPwd, u.getPasswordHash()))
            throw new IllegalArgumentException("Неправильний поточний пароль");

        u.setPasswordHash(pwdEncoder.encode(newPwd));
        userRepository.save(u);
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toDto(user);
    }
}
