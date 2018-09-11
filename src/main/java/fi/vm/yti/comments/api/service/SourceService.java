package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.SourceDTO;

public interface SourceService {

    Set<SourceDTO> findAll();

    SourceDTO findById(final UUID sourceId);

    SourceDTO addOrUpdateSourceFromDto(final SourceDTO fromSource);

    Set<SourceDTO> addOrUpdateSourcesFromDtos(final Set<SourceDTO> fromSources);
}
