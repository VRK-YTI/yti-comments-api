package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentThreadRepository;
import fi.vm.yti.comments.api.security.AuthorizationManager;

@Component
public class CommentThreadDaoImpl implements CommentThreadDao {

    private final CommentThreadRepository commentThreadRepository;
    private final AuthorizationManager authorizationManager;

    @Inject
    public CommentThreadDaoImpl(final CommentThreadRepository commentThreadRepository,
                                final DtoMapper dtoMapper,
                                final AuthorizationManager authorizationManager) {
        this.commentThreadRepository = commentThreadRepository;
        this.authorizationManager = authorizationManager;
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
        if (!commentRound.getId().equals(fromCommentThread.getCommentRound().getId())) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Comment thread data has invalid comment round id."));
        }
    }

    private CommentThread createOrUpdateCommentThread(final CommentRound commentRound,
                                                      final CommentThreadDTO fromCommentThread) {
        validateCommentRound(commentRound, fromCommentThread);
        final CommentThread existingCommentThread;
        if (fromCommentThread.getId() != null) {
            existingCommentThread = commentThreadRepository.findById(fromCommentThread.getId());
        } else {
            existingCommentThread = null;
        }
        final CommentThread commentThread;
        if (existingCommentThread != null) {
            commentThread = updateCommentThread(existingCommentThread, fromCommentThread);
        } else {
            commentThread = createCommentThread(commentRound, fromCommentThread);
        }
        return commentThread;

    }

    private CommentThread createCommentThread(final CommentRound commentRound,
                                              final CommentThreadDTO fromCommentThread) {
        final CommentThread commentThread = new CommentThread();
        commentThread.setId(UUID.randomUUID());
        commentThread.setUserId(authorizationManager.getUserId());
        commentThread.setLabel(fromCommentThread.getLabel());
        commentThread.setDefinition(fromCommentThread.getDefinition());
        commentThread.setResourceUri(fromCommentThread.getResourceUri());
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
        existingCommentThread.setDefinition(fromCommentThread.getDefinition());
        existingCommentThread.setResourceUri(fromCommentThread.getResourceUri());
        existingCommentThread.setProposedStatus(fromCommentThread.getProposedStatus());
        existingCommentThread.setProposedText(fromCommentThread.getProposedText());
        return existingCommentThread;
    }
}
