package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.dao.SourceDao;
import fi.vm.yti.comments.api.dto.DtoMapperService;
import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.service.SourceService;

@Component
public class SourceServiceImpl implements SourceService {

    private final SourceDao sourceDao;
    private final DtoMapperService dtoMapperService;

    public SourceServiceImpl(final SourceDao sourceDao,
                             final DtoMapperService dtoMapperService) {
        this.sourceDao = sourceDao;
        this.dtoMapperService = dtoMapperService;
    }

    @Transactional
    public Set<SourceDTO> findAll() {
        return dtoMapperService.mapSources(sourceDao.findAll());
    }

    @Transactional
    public SourceDTO findById(final UUID sourceId) {
        return dtoMapperService.mapSource(sourceDao.findById(sourceId));
    }

    @Transactional
    public SourceDTO addOrUpdateSourceFromDto(final SourceDTO fromSource) {
        return dtoMapperService.mapSource(sourceDao.addOrUpdateSourceFromDto(fromSource));
    }

    @Transactional
    public Set<SourceDTO> addOrUpdateSourcesFromDtos(final Set<SourceDTO> fromSources) {
        return dtoMapperService.mapSources(sourceDao.addOrUpdateSourcesFromDtos(fromSources));
    }
}
