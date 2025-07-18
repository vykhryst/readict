package ua.nure.readict.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.nure.readict.dto.TropeDto;
import ua.nure.readict.entity.Trope;
import ua.nure.readict.exception.FieldNotUniqueException;
import ua.nure.readict.mapper.TropeMapper;
import ua.nure.readict.repository.TropeRepository;
import ua.nure.readict.service.interfaces.TropeService;
import ua.nure.readict.util.Constants;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TropeServiceImpl extends AbstractService implements TropeService {

    private final TropeRepository tropeRepository;
    private final TropeMapper tropeMapper;

    @Override
    public List<TropeDto> getAll() {
        return tropeRepository.findAll()
                .stream()
                .map(tropeMapper::toDto)
                .toList();
    }

    @Override
    public TropeDto getById(Long id) {
        Trope trope = findEntityByIdOrThrow(id, tropeRepository, Constants.TROPE_NOT_FOUND);
        return tropeMapper.toDto(trope);
    }

    @Override
    public TropeDto create(TropeDto tropeDto) {
        if (tropeRepository.existsByName(tropeDto.name())) {
            throw new FieldNotUniqueException(String.format("Trope with name '%s' already exists.", tropeDto.name()));
        }
        Trope trope = tropeMapper.toEntity(tropeDto);
        Trope savedTrope = tropeRepository.save(trope);
        return tropeMapper.toDto(savedTrope);
    }

    @Override
    public TropeDto update(Long id, TropeDto tropeDto) {
        if (tropeRepository.existsByName(tropeDto.name())) {
            throw new FieldNotUniqueException(String.format("Trope with name '%s' already exists.", tropeDto.name()));
        }
        Trope existingTrope = findEntityByIdOrThrow(id, tropeRepository, Constants.TROPE_NOT_FOUND);
        tropeMapper.partialUpdate(tropeDto, existingTrope);
        Trope updatedTrope = tropeRepository.save(existingTrope);
        return tropeMapper.toDto(updatedTrope);
    }

    @Override
    public void deleteById(Long id) {
        checkEntityExistsOrThrow(id, tropeRepository, Constants.TROPE_NOT_FOUND);
        tropeRepository.deleteById(id);
    }
}
