package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.GlobalCommentsDao;
import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.service.GlobalCommentsService;

@Component
public class GlobalCommentsServiceImpl implements GlobalCommentsService {

    private final GlobalCommentsDao globalCommentsDao;
    private final DtoMapper dtoMapper;

    public GlobalCommentsServiceImpl(final GlobalCommentsDao globalCommentsDao,
                                        final DtoMapper dtoMapper) {
        this.globalCommentsDao = globalCommentsDao;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Set<GlobalCommentsDTO> findAll() {
        return dtoMapper.mapDeepGlobalComments(globalCommentsDao.findAll());
    }

    @Transactional
    public GlobalCommentsDTO findById(final UUID globalCommentsId) {
        return dtoMapper.mapDeepGlobalComments(globalCommentsDao.findById(globalCommentsId));
    }

    @Transactional
    public GlobalCommentsDTO addOrUpdateGlobalCommentsFromDto(final GlobalCommentsDTO fromGlobalComments) {
        return dtoMapper.mapDeepGlobalComments(globalCommentsDao.addOrUpdateGlobalCommentsFromDto(fromGlobalComments));
    }

    @Transactional
    public Set<GlobalCommentsDTO> addOrUpdateGlobalCommentsFromDtos(final Set<GlobalCommentsDTO> fromGlobalComments) {
        return dtoMapper.mapDeepGlobalComments(globalCommentsDao.addOrUpdateGlobalCommentsFromDtos(fromGlobalComments));
    }
}
