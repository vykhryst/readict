package ua.nure.readict.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.GenreDto;
import ua.nure.readict.entity.Genre;
import ua.nure.readict.exception.FieldNotUniqueException;
import ua.nure.readict.mapper.GenreMapper;
import ua.nure.readict.repository.GenreRepository;
import ua.nure.readict.service.interfaces.GenreService;
import ua.nure.readict.util.Constants;
import ua.nure.readict.util.SortingUtil;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl extends AbstractService implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public Page<GenreDto> getAll(String name, int page, int size, String sort) {
        Sort defaultSort = SortingUtil.getSort(
                sort, "name", Sort.Direction.ASC, Genre.class
        );
        Pageable pageable = PageRequest.of(page, size, defaultSort);

        Page<Genre> genres = name != null && !name.isBlank()
                ? genreRepository.findByNameContainingIgnoreCase(name, pageable)
                : genreRepository.findAll(pageable);

        return genres.map(genreMapper::toDto);
    }

    @Override
    public GenreDto getById(Long id) {
        Genre genre = findEntityByIdOrThrow(id, genreRepository, Constants.GENRE_NOT_FOUND);
        return genreMapper.toDto(genre);
    }

    @Override
    public GenreDto create(GenreDto genreDto) {
        if (genreRepository.existsByName(genreDto.name())) {
            throw new FieldNotUniqueException(String.format("Genre with name '%s' already exists.", genreDto.name()));
        }
        Genre genre = genreMapper.toEntity(genreDto);
        return genreMapper.toDto(genreRepository.save(genre));
    }

    @Override
    public GenreDto update(Long id, GenreDto genreDto) {
        if (genreRepository.existsByName(genreDto.name())) {
            throw new FieldNotUniqueException(String.format("Genre with name '%s' already exists.", genreDto.name()));
        }
        Genre existingGenre = findEntityByIdOrThrow(id, genreRepository, Constants.GENRE_NOT_FOUND);
        genreMapper.partialUpdate(genreDto, existingGenre);
        return genreMapper.toDto(genreRepository.save(existingGenre));
    }

    @Override
    public void deleteById(Long id) {
        checkEntityExistsOrThrow(id, genreRepository, Constants.GENRE_NOT_FOUND);
        genreRepository.deleteById(id);
    }
}
