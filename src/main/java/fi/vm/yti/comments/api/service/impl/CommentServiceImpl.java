package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.service.CommentService;

@Component
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final DtoMapper dtoMapper;

    public CommentServiceImpl(final CommentDao commentDao,
                              final DtoMapper dtoMapper) {
        this.commentDao = commentDao;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Set<CommentDTO> findAll() {
        return dtoMapper.mapDeepComments(commentDao.findAll());
    }

    @Transactional
    public CommentDTO findById(final UUID commentId) {
        return dtoMapper.mapDeepComment(commentDao.findById(commentId));
    }

    @Transactional
    public Set<CommentDTO> findByGlobalCommentsId(final UUID globalCommentsId) {
        return dtoMapper.mapDeepComments(commentDao.findByGlobalCommentsId(globalCommentsId));

    }

    @Transactional
    public Set<CommentDTO> findByCommentRoundId(final UUID commentRoundId) {
        return dtoMapper.mapDeepComments(commentDao.findByCommentRoundId(commentRoundId));
    }

    @Transactional
    public CommentDTO addOrUpdateCommentFromDto(final CommentDTO fromComment) {
        return dtoMapper.mapDeepComment(commentDao.addOrUpdateCommentFromDto(fromComment));
    }

    @Transactional
    public Set<CommentDTO> addOrUpdateCommentsFromDtos(final Set<CommentDTO> fromComments) {
        return dtoMapper.mapDeepComments(commentDao.addOrUpdateCommentsFromDtos(fromComments));
    }
}
