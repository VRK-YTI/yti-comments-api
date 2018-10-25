package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentRepository;
import fi.vm.yti.comments.api.security.AuthorizationManager;

@Component
public class CommentDaoImpl implements CommentDao {

    private final CommentRepository commentRepository;
    private final CommentThreadDao commentThreadDao;
    private final AuthorizationManager authorizationManager;

    @Inject
    public CommentDaoImpl(final CommentRepository commentRepository,
                          final CommentThreadDao commentThreadDao,
                          final AuthorizationManager authorizationManager) {
        this.commentRepository = commentRepository;
        this.commentThreadDao = commentThreadDao;
        this.authorizationManager = authorizationManager;
    }

    @Transactional
    public Set<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Transactional
    public Comment findById(final UUID commentId) {
        return commentRepository.findById(commentId);
    }

    @Transactional
    public Set<Comment> findCommentRoundMainLevelCommentsForUserId(final UUID commentRoundId,
                                                                   final UUID userId) {
        return commentRepository.findByCommentThreadCommentRoundIdAndUserId(commentRoundId, userId);
    }

    @Transactional
    public Set<Comment> findByCommentThreadId(final UUID commentThreadId) {
        return commentRepository.findByCommentThreadIdOrderByCreatedAsc(commentThreadId);
    }

    @Transactional
    public Comment addOrUpdateCommentFromDto(final CommentThread commentThread,
                                             final CommentDTO fromComment) {
        final Comment comment = createOrUpdateComment(commentThread, fromComment);
        commentRepository.save(comment);
        return comment;
    }

    @Transactional
    public Set<Comment> addOrUpdateCommentsFromDtos(final CommentThread commentThread,
                                                    final Set<CommentDTO> fromComments) {
        final Set<Comment> comments = new HashSet<>();
        for (final CommentDTO fromComment : fromComments) {
            comments.add(createOrUpdateComment(commentThread, fromComment));
        }
        commentRepository.saveAll(comments);
        return comments;
    }

    @Transactional
    public Set<Comment> addOrUpdateCommentsFromDtos(final CommentRound commentRound,
                                                    final Set<CommentDTO> fromComments) {
        final Set<Comment> comments = new HashSet<>();
        for (final CommentDTO fromComment : fromComments) {
            comments.add(createOrUpdateComment(commentRound, fromComment));
        }
        commentRepository.saveAll(comments);
        return comments;
    }

    private void validateCommentThread(final CommentThread commentThread,
                                       final CommentDTO fromComment) {
        if (!commentThread.getId().equals(fromComment.getCommentThread().getId())) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Comment DTO data has invalid comment thread id."));
        }
    }

    private Comment createOrUpdateComment(final CommentThread commentThread,
                                          final CommentDTO fromComment) {
        validateCommentThread(commentThread, fromComment);
        final Comment existingComment;
        if (fromComment.getId() != null) {
            existingComment = commentRepository.findById(fromComment.getId());
        } else {
            existingComment = null;
        }
        final Comment comment;
        if (existingComment != null) {
            if (!existingComment.getUserId().equals(authorizationManager.getUserId())) {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "User is not allowed to modify existing content from another user."));
            }
            comment = updateComment(existingComment, fromComment);
        } else {
            comment = createComment(commentThread, fromComment);
        }
        return comment;
    }

    private Comment createOrUpdateComment(final CommentRound commentRound,
                                          final CommentDTO fromComment) {
        final CommentThread commentThread = commentThreadDao.findByCommentRoundAndId(commentRound, fromComment.getCommentThread().getId());
        if (commentThread == null) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Comment DTO data has invalid comment thread id."));
        }
        validateCommentThread(commentThread, fromComment);
        final Comment existingComment;
        if (fromComment.getCommentThread() != null && fromComment.getCommentThread().getId() != null) {
            existingComment = commentRepository.findByCommentThreadIdAndUserIdAndParentCommentIsNull(fromComment.getCommentThread().getId(), authorizationManager.getUserId());
        } else {
            existingComment = null;
        }
        final Comment comment;
        if (existingComment != null) {
            comment = updateComment(existingComment, fromComment);
        } else {
            comment = createComment(commentThread, fromComment);
        }
        return comment;
    }

    private Comment createComment(final CommentThread commentThread,
                                  final CommentDTO fromComment) {
        final Comment comment = new Comment();
        comment.setId(fromComment.getId() != null ? fromComment.getId() : UUID.randomUUID());
        comment.setUserId(authorizationManager.getUserId());
        comment.setContent(fromComment.getContent());
        comment.setProposedStatus(fromComment.getProposedStatus() != null && "NOSTATUS".equalsIgnoreCase(fromComment.getProposedStatus()) ? fromComment.getProposedStatus() : null);
        comment.setCommentThread(commentThread);
        resolveParentComment(comment, fromComment);
        final LocalDateTime timeStamp = LocalDateTime.now();
        comment.setCreated(timeStamp);
        return comment;
    }

    private Comment updateComment(final Comment existingComment,
                                  final CommentDTO fromComment) {
        existingComment.setContent(fromComment.getContent());
        existingComment.setProposedStatus(fromComment.getProposedStatus() != null && "NOSTATUS".equalsIgnoreCase(fromComment.getProposedStatus()) ? fromComment.getProposedStatus() : null);
        resolveParentComment(existingComment, fromComment);
        return existingComment;
    }

    private void resolveParentComment(final Comment comment,
                                      final CommentDTO fromComment) {
        final CommentDTO parentCommentDto = fromComment.getParentComment();
        if (parentCommentDto != null && parentCommentDto.getId() != null) {
            final Comment parentComment = findById(parentCommentDto.getId());
            if (parentComment != null) {
                comment.setParentComment(parentComment);
            } else {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid parentComment in DTO data."));
            }
        }
    }
}
