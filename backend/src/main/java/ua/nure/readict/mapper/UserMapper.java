package ua.nure.readict.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.nure.readict.dto.UserDto;
import ua.nure.readict.entity.Genre;
import ua.nure.readict.entity.User;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Genre.class, Collectors.class})
public interface UserMapper {

    User toEntity(UserDto userDto);

    @Mapping(target = "favouriteGenreIds",
            expression = """
                        java( user.getFavouriteGenres()
                                  .stream()
                                  .map(Genre::getId)
                                  .collect(Collectors.toSet()) )
                    """)
    UserDto toDto(User user);
}
