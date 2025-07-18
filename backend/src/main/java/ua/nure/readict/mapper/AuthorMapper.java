package ua.nure.readict.mapper;

import org.mapstruct.*;
import ua.nure.readict.dto.AuthorDto;
import ua.nure.readict.entity.Author;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {
    Author toEntity(AuthorDto authorDto);

    AuthorDto toDto(Author author);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Author partialUpdate(AuthorDto authorDto, @MappingTarget Author author);
}