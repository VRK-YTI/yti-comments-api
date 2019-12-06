package fi.vm.yti.comments.api.service.impl;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.DtoMapperService;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.service.CommentService;

@Component
public class CommentServiceImpl implements CommentService {

    private final CommentRoundDao commentRoundDao;
    private final CommentThreadDao commentThreadDao;
    private final CommentDao commentDao;
    private final DtoMapperService dtoMapperService;

    public CommentServiceImpl(final CommentRoundDao commentRoundDao,
                              final CommentThreadDao commentThreadDao,
                              final CommentDao commentDao,
                              final DtoMapperService dtoMapperService) {
        this.commentRoundDao = commentRoundDao;
        this.commentThreadDao = commentThreadDao;
        this.commentDao = commentDao;
        this.dtoMapperService = dtoMapperService;
    }

    @Transactional
    public Set<CommentDTO> findAll() {
        return dtoMapperService.mapDeepComments(commentDao.findAll());
    }

    @Transactional
    public CommentDTO findById(final UUID commentId) {
        return dtoMapperService.mapDeepCommentWithCommentRound(commentDao.findById(commentId));
    }

    @Transactional
    public CommentDTO findByCommentThreadIdAndCommentIdentifier(final UUID commentRoundId,
                                                                final String commentIdentifier) {
        return dtoMapperService.mapDeepCommentWithCommentRound(commentDao.findByCommentThreadIdAndCommentIdentifier(commentRoundId, commentIdentifier));
    }

    @Transactional
    public Set<CommentDTO> findCommentRoundMainLevelCommentsForUserId(final UUID commentRoundId,
                                                                      final UUID userId) {
        return dtoMapperService.mapDeepComments(commentDao.findCommentRoundMainLevelCommentsForUserId(commentRoundId, userId));
    }

    @Transactional
    public Set<CommentDTO> findByCommentThreadId(final UUID commentThreadId) {
        return dtoMapperService.mapDeepComments(commentDao.findByCommentThreadId(commentThreadId));
    }

    @Transactional
    public CommentDTO addOrUpdateCommentFromDto(final UUID commentRoundId,
                                                final UUID commentThreadId,
                                                final CommentDTO fromComment) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            final CommentThread commentThread = commentThreadDao.findById(commentThreadId);
            if (commentThread != null) {
                return dtoMapperService.mapDeepComment(commentDao.addOrUpdateCommentFromDto(commentThread, fromComment));
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
                return dtoMapperService.mapDeepComments(commentDao.addOrUpdateCommentsFromDtos(commentThread, fromComments));
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional
    public Set<CommentDTO> addOrUpdateCommentsFromDtos(final UUID commentRoundId,
                                                       final Set<CommentDTO> fromComments) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            return dtoMapperService.mapDeepComments(commentDao.addOrUpdateCommentsFromDtos(commentRound, fromComments));
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional
    public void deleteComment(Comment comment) {
        commentDao.deleteComment(comment);
    }

    @Transactional
    public boolean commentHasChildren(Comment comment) {
        return commentDao.commentHasChildren(comment);
    }
}
