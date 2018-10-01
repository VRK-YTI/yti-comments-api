package fi.vm.yti.comments.api.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.entity.Source;

public interface SourceDao {

    Set<Source> findAll();

    Source findById(final UUID sourceId);

    Source getOrCreateByDto(final SourceDTO sourceDto);

    Source addOrUpdateSourceFromDto(final SourceDTO sourcDto);

    Set<Source> addOrUpdateSourcesFromDtos(final Set<SourceDTO> sourcDtos);
}
