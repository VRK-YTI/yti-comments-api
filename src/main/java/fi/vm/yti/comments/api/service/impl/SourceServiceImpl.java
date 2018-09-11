package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.SourceDao;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.service.SourceService;

@Component
public class SourceServiceImpl implements SourceService {

    private final SourceDao sourceDao;
    private final DtoMapper dtoMapper;

    public SourceServiceImpl(final SourceDao sourceDao,
                             final DtoMapper dtoMapper) {
        this.sourceDao = sourceDao;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Set<SourceDTO> findAll() {
        return dtoMapper.mapDeepSources(sourceDao.findAll());
    }

    @Transactional
    public SourceDTO findById(final UUID sourceId) {
        return dtoMapper.mapDeepSource(sourceDao.findById(sourceId));
    }

    @Transactional
    public SourceDTO addOrUpdateSourceFromDto(final SourceDTO fromSource) {
        return dtoMapper.mapDeepSource(sourceDao.addOrUpdateSourceFromDto(fromSource));
    }

    @Transactional
    public Set<SourceDTO> addOrUpdateSourcesFromDtos(final Set<SourceDTO> fromSources) {
        return dtoMapper.mapDeepSources(sourceDao.addOrUpdateSourcesFromDtos(fromSources));
    }
}
