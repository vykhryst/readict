package ua.nure.readict.service.interfaces;

import org.springframework.data.domain.Page;
import ua.nure.readict.dto.SeriesDto;
import ua.nure.readict.dto.book.BookResponse;

import java.util.List;
import java.util.Map;

public interface SeriesService {
    List<SeriesDto> getAll();

    SeriesDto getById(Long id);

    SeriesDto create(SeriesDto seriesDto);

    SeriesDto update(Long id, SeriesDto seriesDto);

    void deleteById(Long id);

    Page<BookResponse> getBooksBySeries(Long id, int page, int size, String sort);

    Map<String, Object> getSeriesStats(Long id);
}
