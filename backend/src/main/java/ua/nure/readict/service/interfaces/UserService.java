package ua.nure.readict.service.interfaces;

import ua.nure.readict.dto.UserDto;
import ua.nure.readict.entity.User;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> findByEmail(String email);

    UserDto findById(Long id);

    void updateFavouriteGenres(Long userId, Set<Long> genreIds);

    User getById(Long id);

    void updateNamesAndGenres(User user, String firstName, String lastName, Set<Long> genreIds);

    void changePassword(Long id, String currentPwd, String newPwd) throws IllegalArgumentException;
}
