package ua.nure.readict.mapper;

import org.mapstruct.*;
import ua.nure.readict.dto.SeriesDto;
import ua.nure.readict.entity.Series;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SeriesMapper {
    Series toEntity(SeriesDto seriesDto);

    SeriesDto toDto(Series series);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Series partialUpdate(SeriesDto seriesDto, @MappingTarget Series series);
}