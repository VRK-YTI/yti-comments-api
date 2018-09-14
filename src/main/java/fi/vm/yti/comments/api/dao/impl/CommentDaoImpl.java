package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.GlobalCommentsDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.GlobalComments;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentRepository;

@Component
public class CommentDaoImpl implements CommentDao {

    private final CommentRepository commentRepository;
    private final GlobalCommentsDao globalCommentsDao;
    private final CommentRoundDao commentRoundDao;

    @Inject
    public CommentDaoImpl(final CommentRepository commentRepository,
                          final GlobalCommentsDao globalCommentsDao,
                          final CommentRoundDao commentRoundDao) {
        this.commentRepository = commentRepository;
        this.globalCommentsDao = globalCommentsDao;
        this.commentRoundDao = commentRoundDao;
    }

    public Set<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Comment findById(final UUID commentId) {
        return commentRepository.findById(commentId);
    }

    public Set<Comment> findByCommentRoundId(final UUID commentRoundId) {
        return commentRepository.findByCommentRoundIdOrderByCreatedAsc(commentRoundId);
    }

    public Set<Comment> findByGlobalCommentsId(final UUID globalCommentsId) {
        return commentRepository.findByGlobalCommentsIdOrderByCreatedAsc(globalCommentsId);
    }

    public Comment addOrUpdateCommentFromDto(final CommentDTO fromComment) {
        final Comment comment = createOrUpdateComment(fromComment);
        commentRepository.save(comment);
        return comment;
    }

    public Set<Comment> addOrUpdateCommentsFromDtos(final Set<CommentDTO> fromComments) {
        final Set<Comment> comments = new HashSet<>();
        for (final CommentDTO fromComment : fromComments) {
            comments.add(createOrUpdateComment(fromComment));
        }
        commentRepository.saveAll(comments);
        return comments;
    }

    private Comment createOrUpdateComment(final CommentDTO fromComment) {
        final Comment existingComment;
        if (fromComment.getId() != null) {
            existingComment = commentRepository.findById(fromComment.getId());
        } else {
            existingComment = null;
        }
        final Comment comment;
        if (existingComment != null) {
            comment = updateComment(existingComment, fromComment);
        } else {
            comment = createComment(fromComment);
        }
        return comment;
    }

    private Comment createComment(final CommentDTO fromComment) {
        final Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setUserId(fromComment.getUserId());
        comment.setResourceUri(fromComment.getResourceUri());
        comment.setResourceSuggestion(fromComment.getResourceSuggestion());
        comment.setContent(fromComment.getContent());
        comment.setProposedStatus(fromComment.getProposedStatus());
        resolveGlobalComments(comment, fromComment);
        resolveCommentRound(comment, fromComment);
        resolveRelatedComment(comment, fromComment);
        final LocalDateTime timeStamp = LocalDateTime.now();
        comment.setCreated(timeStamp);
        return comment;
    }

    private Comment updateComment(final Comment existingComment,
                                  final CommentDTO fromComment) {
        existingComment.setUserId(fromComment.getUserId());
        existingComment.setResourceUri(fromComment.getResourceUri());
        existingComment.setResourceSuggestion(fromComment.getResourceSuggestion());
        existingComment.setContent(fromComment.getContent());
        existingComment.setProposedStatus(fromComment.getProposedStatus());
        resolveGlobalComments(existingComment, fromComment);
        resolveCommentRound(existingComment, fromComment);
        resolveRelatedComment(existingComment, fromComment);
        return existingComment;
    }

    private void resolveGlobalComments(final Comment comment,
                                       final CommentDTO fromComment) {
        final GlobalCommentsDTO globalCommentsDto = fromComment.getGlobalComments();
        if (globalCommentsDto != null && globalCommentsDto.getId() != null) {
            final GlobalComments globalComments = globalCommentsDao.findById(globalCommentsDto.getId());
            if (globalComments != null) {
                comment.setGlobalComments(globalComments);
            } else {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid globalComments in DTO data."));
            }
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid globalComments in DTO data."));
        }
    }

    private void resolveRelatedComment(final Comment comment,
                                       final CommentDTO fromComment) {
        final CommentDTO relatedCommentDto = fromComment.getRelatedComment();
        if (relatedCommentDto != null && relatedCommentDto.getId() != null) {
            final Comment relatedComment = findById(relatedCommentDto.getId());
            if (relatedComment != null) {
                comment.setRelatedComment(relatedComment);
            } else {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid relatedComment in DTO data."));
            }
        }
    }

    private void resolveCommentRound(final Comment comment,
                                     final CommentDTO fromComment) {
        final CommentRoundDTO commentRoundDto = fromComment.getCommentRound();
        if (commentRoundDto != null && commentRoundDto.getId() != null) {
            final CommentRound commentRound = commentRoundDao.findById(commentRoundDto.getId());
            if (commentRound != null) {
                comment.setCommentRound(commentRound);
            } else {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid commentRound in DTO data."));
            }
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid commentRound in DTO data."));
        }
    }
}
