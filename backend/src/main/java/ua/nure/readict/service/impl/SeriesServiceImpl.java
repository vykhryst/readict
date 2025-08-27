package ua.nure.readict.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.SeriesDto;
import ua.nure.readict.dto.book.BookResponse;
import ua.nure.readict.entity.Book;
import ua.nure.readict.entity.Series;
import ua.nure.readict.exception.FieldNotUniqueException;
import ua.nure.readict.mapper.BookMapper;
import ua.nure.readict.mapper.SeriesMapper;
import ua.nure.readict.repository.BookRepository;
import ua.nure.readict.repository.SeriesRepository;
import ua.nure.readict.service.interfaces.SeriesService;
import ua.nure.readict.util.Constants;
import ua.nure.readict.util.SortingUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl extends AbstractService implements SeriesService {

    private final SeriesRepository seriesRepository;
    private final SeriesMapper seriesMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<SeriesDto> getAll() {
        return seriesRepository.findAll()
                .stream()
                .map(seriesMapper::toDto)
                .toList();
    }

    @Override
    public SeriesDto getById(Long id) {
        Series series = findEntityByIdOrThrow(id, seriesRepository, Constants.SERIES_NOT_FOUND);
        return seriesMapper.toDto(series);
    }

    @Override
    public SeriesDto create(SeriesDto seriesDto) {
        if (seriesRepository.existsByName(seriesDto.name())) {
            throw new FieldNotUniqueException(String.format("Series with name '%s' already exists.", seriesDto.name()));
        }
        Series series = seriesMapper.toEntity(seriesDto);
        Series savedSeries = seriesRepository.save(series);
        return seriesMapper.toDto(savedSeries);
    }

    @Override
    public SeriesDto update(Long id, SeriesDto seriesDto) {
        if (seriesRepository.existsByName(seriesDto.name())) {
            throw new FieldNotUniqueException(String.format("Series with name '%s' already exists.", seriesDto.name()));
        }
        Series existingSeries = findEntityByIdOrThrow(id, seriesRepository, Constants.SERIES_NOT_FOUND);
        seriesMapper.partialUpdate(seriesDto, existingSeries);
        Series updatedSeries = seriesRepository.save(existingSeries);
        return seriesMapper.toDto(updatedSeries);
    }

    @Override
    public void deleteById(Long id) {
        checkEntityExistsOrThrow(id, seriesRepository, Constants.SERIES_NOT_FOUND);
        seriesRepository.deleteById(id);
    }

    @Override
    public Page<BookResponse> getBooksBySeries(Long seriesId, int page, int size, String sort) {
        findEntityByIdOrThrow(seriesId, seriesRepository, Constants.SERIES_NOT_FOUND);

        Sort defaultSort = SortingUtil.getSort(
                sort,
                "seriesNumber",
                Sort.Direction.ASC,
                Book.class
        );

        Pageable pageable = PageRequest.of(page, size, defaultSort);

        Page<Book> booksPage = bookRepository.findAll((root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            return buildBooksBySeriesPredicates(root, cb, seriesId);
        }, pageable);

        return booksPage.map(bookMapper::toResponse);
    }

    @Override
    public Map<String, Object> getSeriesStats(Long seriesId) {
        findEntityByIdOrThrow(seriesId, seriesRepository, Constants.SERIES_NOT_FOUND);

        Map<String, Object> stats = new HashMap<>();

        Long bookCount = bookRepository.countBySeriesId(seriesId);
        Double averageRating = bookRepository.getAverageRatingBySeriesId(seriesId);

        stats.put("bookCount", bookCount != null ? bookCount : 0);
        stats.put("averageRating", averageRating != null ? averageRating : 0.0);

        return stats;
    }


    private Predicate buildSeriesPredicates(Root<Series> root,
                                            CriteriaBuilder cb,
                                            String name) {
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            predicates.add(
                    cb.like(cb.lower(root.get("name")),
                            "%" + name.toLowerCase() + "%"));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildBooksBySeriesPredicates(Root<Book> root,
                                                   CriteriaBuilder cb,
                                                   Long seriesId) {
        List<Predicate> predicates = new ArrayList<>();

        if (seriesId != null) {
            predicates.add(cb.equal(root.get("series").get("id"), seriesId));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
