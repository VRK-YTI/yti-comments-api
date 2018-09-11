package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentRoundGroupDao;
import fi.vm.yti.comments.api.dto.CommentRoundGroupDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.service.CommentRoundGroupService;

@Component
public class CommentRoundGroupServiceImpl implements CommentRoundGroupService {

    private final CommentRoundGroupDao commentRoundGroupDao;
    private final DtoMapper dtoMapper;

    public CommentRoundGroupServiceImpl(final CommentRoundGroupDao commentRoundGroupDao,
                                        final DtoMapper dtoMapper) {
        this.commentRoundGroupDao = commentRoundGroupDao;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Set<CommentRoundGroupDTO> findAll() {
        return dtoMapper.mapDeepCommentRoundGroups(commentRoundGroupDao.findAll());
    }

    @Transactional
    public CommentRoundGroupDTO findById(final UUID commentRoundGroupId) {
        return dtoMapper.mapDeepCommentRoundGroup(commentRoundGroupDao.findById(commentRoundGroupId));
    }

    @Transactional
    public CommentRoundGroupDTO addOrUpdateCommentRoundGroupFromDto(final CommentRoundGroupDTO fromCommentRoundGroup) {
        return dtoMapper.mapDeepCommentRoundGroup(commentRoundGroupDao.addOrUpdateCommentRoundGroupFromDto(fromCommentRoundGroup));
    }

    @Transactional
    public Set<CommentRoundGroupDTO> addOrUpdateCommentRoundGroupsFromDtos(final Set<CommentRoundGroupDTO> fromCommentRoundGroups) {
        return dtoMapper.mapDeepCommentRoundGroups(commentRoundGroupDao.addOrUpdateCommentRoundGroupsFromDtos(fromCommentRoundGroups));
    }
}
