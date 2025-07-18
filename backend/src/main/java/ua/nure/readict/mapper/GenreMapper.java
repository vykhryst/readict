package ua.nure.readict.mapper;

import org.mapstruct.*;
import ua.nure.readict.dto.GenreDto;
import ua.nure.readict.entity.Genre;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {
    Genre toEntity(GenreDto genreDto);

    GenreDto toDto(Genre genre);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Genre partialUpdate(GenreDto genreDto, @MappingTarget Genre genre);
}