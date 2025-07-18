package ua.nure.readict.service.interfaces;

import org.springframework.data.domain.Page;
import ua.nure.readict.dto.GenreDto;

public interface GenreService {
    Page<GenreDto> getAll(String name, int page, int size, String sort);
    GenreDto getById(Long id);
    GenreDto create(GenreDto genreDto);
    GenreDto update(Long id, GenreDto genreDto);
    void deleteById(Long id);
}
