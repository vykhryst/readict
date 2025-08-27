package ua.nure.readict.service.interfaces;

import ua.nure.readict.dto.UserDto;

import java.util.Set;

public interface UserService {

    void updateFavouriteGenres(Long userId, Set<Long> genreIds);

    UserDto getById(Long id);

    UserDto updateUserNamesAndFavouriteGenres(Long userId, String firstName, String lastName, Set<Long> genreIds);

    void changePassword(Long id, String currentPwd, String newPwd) throws IllegalArgumentException;
}
