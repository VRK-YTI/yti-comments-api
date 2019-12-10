package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.api.ApiUtils;
import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentRepository;
import fi.vm.yti.comments.api.security.AuthorizationManager;
import static fi.vm.yti.comments.api.exception.ErrorConstants.*;
import static fi.vm.yti.comments.api.utils.StringUtils.parseIntegerFromString;
import static fi.vm.yti.comments.api.utils.StringUtils.parseUuidFromString;

@Component
public class CommentDaoImpl implements CommentDao {

    private static final String PREFIX_FOR_COMMENTS_SEQUENCE = "seq_thread_comments_";

    private final CommentRepository commentRepository;
    private final CommentThreadDao commentThreadDao;
    private final CommentRoundDao commentRoundDao;
    private final AuthorizationManager authorizationManager;
    private final ApiUtils apiUtils;

    @Inject
    public CommentDaoImpl(final CommentRepository commentRepository,
                          final CommentThreadDao commentThreadDao,
                          final CommentRoundDao commentRoundDao,
                          final AuthorizationManager authorizationManager,
                          final ApiUtils apiUtils) {
        this.commentRepository = commentRepository;
        this.commentThreadDao = commentThreadDao;
        this.commentRoundDao = commentRoundDao;
        this.authorizationManager = authorizationManager;
        this.apiUtils = apiUtils;
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
    public Comment findByCommentThreadIdAndCommentIdentifier(final UUID commentThreadId,
                                                             final String commentIdentifier) {
        final UUID commentId = parseUuidFromString(commentIdentifier);
        if (commentId != null) {
            return findById(commentId);
        } else {
            final Integer commentSequenceId = parseIntegerFromString(commentIdentifier);
            if (commentSequenceId != null) {
                return findByCommentThreadIdAndSequenceId(commentThreadId, commentSequenceId);
            }
        }
        return null;
    }

    @Transactional
    public Comment findByCommentThreadIdAndSequenceId(final UUID commentThreadId,
                                                      final Integer commentSequenceId) {
        return commentRepository.findByCommentThreadIdAndSequenceId(commentThreadId, commentSequenceId);
    }

    @Transactional
    public Set<Comment> findCommentRoundMainLevelCommentsForUserId(final UUID commentRoundId,
                                                                   final UUID userId) {
        return commentRepository.findByCommentThreadCommentRoundIdAndUserIdAndParentCommentIsNull(commentRoundId, userId);
    }

    @Transactional
    public Set<Comment> findByCommentThreadId(final UUID commentThreadId) {
        return commentRepository.findByCommentThreadIdOrderByCreatedAsc(commentThreadId);
    }

    @Transactional
    public Set<Comment> findByCommentThreadIdAndParentCommentIsNull(final UUID commentThreadId) {
        return commentRepository.findByCommentThreadIdAndParentCommentIsNullOrderByCreatedAsc(commentThreadId);
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
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_COMMENT_HAS_INVALID_COMMENTTHREAD_ID));
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
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_NOT_ALLOWED_TO_MODIFY_CONTENT_FROM_ANOTHER_USER));
            }
            comment = updateComment(existingComment, fromComment);
        } else {
            comment = createComment(commentThread, fromComment);
        }
        updateTimeStamps(commentThread.getCommentRound().getId(), commentThread.getId());
        return comment;
    }

    private Comment createOrUpdateComment(final CommentRound commentRound,
                                          final CommentDTO fromComment) {
        final CommentThread commentThread = commentThreadDao.findByCommentRoundAndId(commentRound, fromComment.getCommentThread().getId());
        if (commentThread == null) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_COMMENT_DOES_NOT_HAVE_COMMENT_THREAD));
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
            if (!fromComment.getContent().equals(existingComment.getContent())) {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_CANNOT_MODIFY_EXISTING_COMMENT));
            }
            comment = updateComment(existingComment, fromComment);
        } else {
            comment = createComment(commentThread, fromComment);
        }
        updateTimeStamps(commentRound.getId(), commentThread.getId());
        return comment;
    }

    private void updateTimeStamps(final UUID commentRoundId,
                                  final UUID commentThreadId) {
        final LocalDateTime now = LocalDateTime.now();
        commentRoundDao.updateContentModified(commentRoundId, now);
        commentThreadDao.updateCommentsModified(commentThreadId, now);
    }

    private Comment createComment(final CommentThread commentThread,
                                  final CommentDTO fromComment) {
        final Comment comment = new Comment();
        comment.setId(fromComment.getId() != null ? fromComment.getId() : UUID.randomUUID());
        comment.setUserId(authorizationManager.getUserId());
        final String content = fromComment.getContent();
        if (content != null && content.trim().length() > 0) {
            comment.setContent(fromComment.getContent());
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_CANNOT_CREATE_EMPTY_COMMENT));
        }
        resolveParentComment(comment, fromComment);
        final String endStatus = fromComment.getEndStatus();
        if (comment.getParentComment() == null && endStatus != null && !"NOSTATUS".equalsIgnoreCase(endStatus)) {
            comment.setProposedStatus(endStatus);
            comment.setEndStatus(endStatus);
        } else if (comment.getParentComment() == null) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_PROPOSED_STATUS_REQUIRED));
        }
        comment.setCommentThread(commentThread);
        final LocalDateTime timeStamp = LocalDateTime.now();
        comment.setCreated(timeStamp);
        comment.setModified(timeStamp);
        final Integer sequenceId = getNextSequenceId(commentThread.getId());
        comment.setSequenceId(sequenceId);
        comment.setUri(apiUtils.createCommentUri(commentThread.getCommentRound().getSequenceId(), commentThread.getSequenceId(), sequenceId));
        return comment;
    }

    private Comment updateComment(final Comment existingComment,
                                  final CommentDTO fromComment) {
        existingComment.setContent(fromComment.getContent());
        resolveParentComment(existingComment, fromComment);
        final String endStatus = fromComment.getEndStatus();
        if (existingComment.getParentComment() == null && endStatus != null && !"NOSTATUS".equalsIgnoreCase(endStatus)) {
            existingComment.setEndStatus(endStatus);
        } else if (existingComment.getParentComment() == null) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_PROPOSED_STATUS_REQUIRED));
        }
        final LocalDateTime timeStamp = LocalDateTime.now();
        existingComment.setModified(timeStamp);
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
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_INVALID_PARENTCOMMENT));
            }
        }
    }

    @Transactional
    public void deleteComment(final Comment comment) {
        Comment theComment = commentRepository.findById(comment.getId());
        commentRepository.delete(theComment);
    }

    @Transactional
    public boolean commentHasChildren(Comment comment) {
        Comment theComment = commentRepository.findById(comment.getId());
        Set<Comment> childComments = commentRepository.findByParentComment(theComment);
        if (childComments.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private Integer getNextSequenceId(final UUID commentRoundId) {
        final String postfix = commentRoundId.toString().replaceAll("-", "_");
        final String sequenceName = PREFIX_FOR_COMMENTS_SEQUENCE + postfix;
        return commentRepository.getNextSequenceId(sequenceName);
    }
}
