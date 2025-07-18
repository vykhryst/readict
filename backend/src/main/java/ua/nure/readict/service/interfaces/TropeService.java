package ua.nure.readict.service.interfaces;

import ua.nure.readict.dto.TropeDto;

import java.util.List;

public interface TropeService {
    List<TropeDto> getAll();
    TropeDto getById(Long id);
    TropeDto create(TropeDto tropeDto);
    TropeDto update(Long id, TropeDto tropeDto);
    void deleteById(Long id);
}
