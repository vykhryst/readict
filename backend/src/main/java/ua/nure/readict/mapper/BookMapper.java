package ua.nure.readict.mapper;

import org.mapstruct.*;
import ua.nure.readict.dto.book.BookRequest;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.entity.Book;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {SeriesMapper.class, AuthorMapper.class, GenreMapper.class, TropeMapper.class})
public interface BookMapper {

    Book toEntity(BookRequest bookRequest);

    BookResponse toResponse(Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Book partialUpdate(BookRequest bookRequest, @MappingTarget Book book);
}
