package ua.nure.readict.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public abstract class AbstractService {

    protected <T> T findEntityByIdOrThrow(Long id, JpaRepository<T, Long> repository, String errorMessage) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(errorMessage, id)));
    }

    protected void checkEntityExistsOrThrow(Long id, JpaRepository<?, Long> repository, String errorMessage) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(String.format(errorMessage, id));
        }
    }

    protected <T> Set<T> findEntitiesByIdsOrThrow(Set<Long> ids, JpaRepository<T, Long> repository, String errorMessage) {
        return ids.stream()
                .map(id -> repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(String.format(errorMessage, id))))
                .collect(Collectors.toSet());
    }

    protected <T> T findEntityByIdOrNull(Long id, JpaRepository<T, Long> repository, String errorMessage) {
        return id == null ? null : findEntityByIdOrThrow(id, repository, errorMessage);
    }
}
