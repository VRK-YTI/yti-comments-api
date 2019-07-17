package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentThreadRepository;
import fi.vm.yti.comments.api.security.AuthorizationManager;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_CANNOT_MODIFY_EXISTING_COMMENTTHREAD;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_COMMENTTHREAD_HAS_INVALID_COMMENTROUND_ID;

@Component
public class CommentThreadDaoImpl implements CommentThreadDao {

    private final CommentThreadRepository commentThreadRepository;
    private final AuthorizationManager authorizationManager;

    @Inject
    public CommentThreadDaoImpl(final CommentThreadRepository commentThreadRepository,
                                final AuthorizationManager authorizationManager) {
        this.commentThreadRepository = commentThreadRepository;
        this.authorizationManager = authorizationManager;
    }

    @Transactional
    public void delete(final CommentThread commentThread) {
        commentThreadRepository.delete(commentThread);
    }

    @Transactional
    public Set<CommentThread> findAll() {
        return commentThreadRepository.findAll();
    }

    @Transactional
    public CommentThread findById(final UUID commentThreadId) {
        return commentThreadRepository.findById(commentThreadId);
    }

    @Transactional
    public CommentThread findByCommentRoundAndId(final CommentRound commentRound,
                                                 final UUID commentThreadId) {
        return commentThreadRepository.findByCommentRoundAndId(commentRound, commentThreadId);
    }

    @Transactional
    public Set<CommentThread> findByCommentRoundId(final UUID commentRoundId) {
        return commentThreadRepository.findByCommentRoundId(commentRoundId);
    }

    @Transactional
    public CommentThread addOrUpdateCommentThreadFromDto(final CommentRound commentRound,
                                                         final CommentThreadDTO fromCommentThread) {
        final CommentThread commentThread = createOrUpdateCommentThread(commentRound, fromCommentThread);
        commentThreadRepository.save(commentThread);
        return commentThread;
    }

    @Transactional
    public Set<CommentThread> addOrUpdateCommentThreadsFromDtos(final CommentRound commentRound,
                                                                final Set<CommentThreadDTO> fromCommentThreads) {
        final Set<CommentThread> commentThreads = new HashSet<>();
        for (final CommentThreadDTO fromCommentThread : fromCommentThreads) {
            commentThreads.add(createOrUpdateCommentThread(commentRound, fromCommentThread));
        }
        commentThreadRepository.saveAll(commentThreads);
        return commentThreads;
    }

    private void validateCommentRound(final CommentRound commentRound,
                                      final CommentThreadDTO fromCommentThread) {
        if (fromCommentThread.getCommentRound() != null && !commentRound.getId().equals(fromCommentThread.getCommentRound().getId())) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_COMMENTTHREAD_HAS_INVALID_COMMENTROUND_ID));
        }
    }

    private CommentThread createOrUpdateCommentThread(final CommentRound commentRound,
                                                      final CommentThreadDTO fromCommentThread) {
        validateCommentRound(commentRound, fromCommentThread);
        final CommentThread existingCommentThread;
        if (fromCommentThread.getId() != null) {
            existingCommentThread = commentThreadRepository.findById(fromCommentThread.getId());
        } else if (fromCommentThread.getResourceUri() != null) {
            existingCommentThread = commentThreadRepository.findByCommentRoundAndResourceUri(commentRound, fromCommentThread.getResourceUri());
        } else {
            existingCommentThread = null;
        }
        final CommentThread commentThread;
        if (existingCommentThread != null) {
            if (!fromCommentThread.getProposedStatus().equals(existingCommentThread.getProposedStatus()) ||
                !Objects.equals(fromCommentThread.getProposedText(), existingCommentThread.getProposedText())) {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_CANNOT_MODIFY_EXISTING_COMMENTTHREAD));
            }
            return existingCommentThread;
        } else {
            commentThread = createCommentThread(commentRound, fromCommentThread);
        }
        return commentThread;

    }

    private CommentThread createCommentThread(final CommentRound commentRound,
                                              final CommentThreadDTO fromCommentThread) {
        final CommentThread commentThread = new CommentThread();
        commentThread.setId(fromCommentThread.getId() != null ? fromCommentThread.getId() : UUID.randomUUID());
        commentThread.setUserId(authorizationManager.getUserId());
        commentThread.setLabel(fromCommentThread.getLabel());
        commentThread.setDescription(fromCommentThread.getDescription());
        commentThread.setLocalName(fromCommentThread.getLocalName());
        commentThread.setResourceUri(fromCommentThread.getResourceUri());
        commentThread.setCurrentStatus(fromCommentThread.getCurrentStatus());
        commentThread.setProposedStatus(fromCommentThread.getProposedStatus());
        commentThread.setProposedText(fromCommentThread.getProposedText());
        final LocalDateTime timeStamp = LocalDateTime.now();
        commentThread.setCreated(timeStamp);
        commentThread.setCommentRound(commentRound);
        return commentThread;
    }

    private CommentThread updateCommentThread(final CommentThread existingCommentThread,
                                              final CommentThreadDTO fromCommentThread) {
        existingCommentThread.setLabel(fromCommentThread.getLabel());
        existingCommentThread.setDescription(fromCommentThread.getDescription());
        existingCommentThread.setLocalName(fromCommentThread.getLocalName());
        existingCommentThread.setResourceUri(fromCommentThread.getResourceUri());
        existingCommentThread.setCurrentStatus(fromCommentThread.getCurrentStatus());
        existingCommentThread.setProposedStatus(fromCommentThread.getProposedStatus());
        existingCommentThread.setProposedText(fromCommentThread.getProposedText());
        return existingCommentThread;
    }

    @Transactional
    public void deleteCommentThread(final CommentThread commentThread) {
        commentThreadRepository.delete(commentThread);
    }
}
