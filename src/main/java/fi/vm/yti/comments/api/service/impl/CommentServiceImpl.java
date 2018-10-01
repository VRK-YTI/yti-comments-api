package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.service.CommentService;

@Component
public class CommentServiceImpl implements CommentService {

    private final CommentRoundDao commentRoundDao;
    private final CommentThreadDao commentThreadDao;
    private final CommentDao commentDao;
    private final DtoMapper dtoMapper;

    public CommentServiceImpl(final CommentRoundDao commentRoundDao,
                              final CommentThreadDao commentThreadDao,
                              final CommentDao commentDao,
                              final DtoMapper dtoMapper) {
        this.commentRoundDao = commentRoundDao;
        this.commentThreadDao = commentThreadDao;
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
    public Set<CommentDTO> findByCommentThreadId(final UUID commentThreadId) {
        return dtoMapper.mapDeepComments(commentDao.findByCommentThreadId(commentThreadId));
    }

    @Transactional
    public CommentDTO addOrUpdateCommentFromDto(final UUID commentRoundId,
                                                final UUID commentThreadId,
                                                final CommentDTO fromComment) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            final CommentThread commentThread = commentThreadDao.findById(commentThreadId);
            if (commentThread != null) {
                return dtoMapper.mapDeepComment(commentDao.addOrUpdateCommentFromDto(commentThread, fromComment));
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional
    public Set<CommentDTO> addOrUpdateCommentsFromDtos(final UUID commentRoundId,
                                                       final UUID commentThreadId,
                                                       final Set<CommentDTO> fromComments) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            final CommentThread commentThread = commentThreadDao.findById(commentThreadId);
            if (commentThread != null) {
                return dtoMapper.mapDeepComments(commentDao.addOrUpdateCommentsFromDtos(commentThread, fromComments));
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }
}
