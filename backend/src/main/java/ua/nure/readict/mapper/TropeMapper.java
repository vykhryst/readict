package ua.nure.readict.mapper;

import org.mapstruct.*;
import ua.nure.readict.dto.TropeDto;
import ua.nure.readict.entity.Trope;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TropeMapper {
    Trope toEntity(TropeDto tropeDto);

    TropeDto toDto(Trope trope);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Trope partialUpdate(TropeDto tropeDto, @MappingTarget Trope trope);
}