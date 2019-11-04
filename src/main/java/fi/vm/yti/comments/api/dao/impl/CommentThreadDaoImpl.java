package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.constants.ApiConstants;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
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
    private final CommentRoundDao commentRoundDao;

    @Inject
    public CommentThreadDaoImpl(final CommentThreadRepository commentThreadRepository,
                                final AuthorizationManager authorizationManager,
                                @Lazy final CommentRoundDao commentRoundDao) {
        this.commentThreadRepository = commentThreadRepository;
        this.authorizationManager = authorizationManager;
        this.commentRoundDao = commentRoundDao;
    }

    @Transactional
    public void saveAll(final Set<CommentThread> commentThreads) {
        commentThreadRepository.saveAll(commentThreads);
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
    public Set<CommentThread> findAll(final PageRequest pageRequest) {
        return new HashSet<>(commentThreadRepository.findAll(pageRequest).getContent());
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
    public Set<CommentThread> findByIds(final Set<UUID> uuids) {
        return commentThreadRepository.findByIdIn(uuids);
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
        return this.addOrUpdateCommentThreadsFromDtos(commentRound, fromCommentThreads, false);
    }

    @Transactional
    public Set<CommentThread> addOrUpdateCommentThreadsFromDtos(final CommentRound commentRound,
                                                                final Set<CommentThreadDTO> fromCommentThreads,
                                                                final boolean removeOrphans) {
        final Set<CommentThread> commentThreads = new HashSet<>();
        if (fromCommentThreads != null) {
            fromCommentThreads.forEach(fromCommentThread -> commentThreads.add(createOrUpdateCommentThread(commentRound, fromCommentThread)));
        }
        if (removeOrphans && ApiConstants.STATUS_INCOMPLETE.equalsIgnoreCase(commentRound.getStatus())) {
            final Set<CommentThread> existingCommentThreads = commentRound.getCommentThreads();
            if (existingCommentThreads != null) {
                final Set<String> newCommentThreadUris = commentThreads.stream().map(CommentThread::getResourceUri).collect(Collectors.toSet());
                final Set<UUID> newCommentThreadIds = commentThreads.stream().map(CommentThread::getId).collect(Collectors.toSet());
                existingCommentThreads.forEach(existingCommentThread -> {
                    if (!newCommentThreadUris.contains(existingCommentThread.getResourceUri()) || !newCommentThreadIds.contains(existingCommentThread.getId())) {
                        existingCommentThread.setCommentRound(null);
                        delete(existingCommentThread);
                    }
                });
                saveAll(commentThreads);
                commentRound.setCommentThreads(commentThreads);
                commentRoundDao.save(commentRound);
            } else {
                saveAll(commentThreads);
                commentRound.setCommentThreads(commentThreads);
                commentRoundDao.save(commentRound);
            }
        } else {
            saveAll(commentThreads);
        }
        return commentThreads;
    }

    @Transactional
    public int getCommentThreadCount(final Set<UUID> commentThreadIds,
                                     final UUID commentRoundId,
                                     final LocalDateTime after,
                                     final LocalDateTime before) {
        if (commentThreadIds != null && after != null && before != null) {
            return commentThreadRepository.getCommentThreadCountWithIdsAndAfterAndBefore(commentThreadIds, after, before);
        } else if (commentThreadIds != null && after != null) {
            return commentThreadRepository.getCommentThreadCountWithIdsAndAfter(commentThreadIds, after);
        } else if (commentThreadIds != null && after != null) {
            return commentThreadRepository.getCommentThreadCountWithIdsAndBefore(commentThreadIds, before);
        } else if (commentRoundId != null && after != null && before != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundIdAndAfterAndBefore(commentRoundId, after, before);
        } else if (commentRoundId != null && after != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundIdAndAfter(commentRoundId, after);
        } else if (commentRoundId != null && before != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundIdAndBefore(commentRoundId, before);
        } else if (commentRoundId != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundId(commentRoundId);
        } else {
            return commentThreadRepository.getCommentThreadCount();
        }
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
        commentRoundDao.updateContentModified(commentRound.getId(), LocalDateTime.now());
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

    @Transactional
    public void deleteCommentThread(final CommentThread commentThread) {
        commentThreadRepository.delete(commentThread);
    }

    @Transactional
    public void updateCommentsModified(final UUID commentThreadId,
                                       final LocalDateTime timeStamp) {
        commentThreadRepository.updateCommentsModified(commentThreadId, timeStamp);
    }
}
