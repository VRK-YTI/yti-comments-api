package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.api.ApiUtils;
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
import static fi.vm.yti.comments.api.utils.StringUtils.parseIntegerFromString;
import static fi.vm.yti.comments.api.utils.StringUtils.parseUuidFromString;

@Component
public class CommentThreadDaoImpl implements CommentThreadDao {

    private final static String PREFIX_FOR_COMMENTTHREADS_SEQUENCE = "seq_round_threads_";

    private final CommentThreadRepository commentThreadRepository;
    private final AuthorizationManager authorizationManager;
    private final CommentRoundDao commentRoundDao;
    private final ApiUtils apiUtils;

    @Inject
    public CommentThreadDaoImpl(final CommentThreadRepository commentThreadRepository,
                                final AuthorizationManager authorizationManager,
                                @Lazy final CommentRoundDao commentRoundDao,
                                final ApiUtils apiUtils) {
        this.commentThreadRepository = commentThreadRepository;
        this.authorizationManager = authorizationManager;
        this.commentRoundDao = commentRoundDao;
        this.apiUtils = apiUtils;
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
    public Set<CommentThread> findByCommentRoundUriInAndUriIn(final Set<String> commentRoundUris,
                                                              final Set<String> uris,
                                                              final LocalDateTime after,
                                                              final LocalDateTime before,
                                                              final PageRequest pageRequest) {
        if (commentRoundUris != null && !commentRoundUris.isEmpty() && uris != null && !uris.isEmpty()) {
            if (after != null && before != null) {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndUriInAndCreatedBetweenOrCommentRoundUriInAndUriInAndCommentsModifiedBetween(commentRoundUris, uris, after, before, commentRoundUris, uris, after, before, pageRequest).getContent());
            } else if (after != null) {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndUriInAndCreatedAfterOrCommentRoundUriInAndUriInAndCommentsModifiedAfter(commentRoundUris, uris, after, commentRoundUris, uris, after, pageRequest).getContent());
            } else if (before != null) {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndUriInAndCreatedBeforeOrCommentRoundUriInAndUriInAndCommentsModifiedAfter(commentRoundUris, uris, before, commentRoundUris, uris, before, pageRequest).getContent());
            } else {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndUriIn(commentRoundUris, uris, pageRequest).getContent());
            }
        } else if (commentRoundUris != null && !commentRoundUris.isEmpty()) {
            if (after != null && before != null) {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndCreatedBetweenOrCommentRoundUriInAndCommentsModifiedBetween(commentRoundUris, after, before, commentRoundUris, after, before, pageRequest).getContent());
            } else if (after != null) {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndCreatedAfterOrCommentRoundUriInAndCommentsModifiedAfter(commentRoundUris, after, commentRoundUris, after, pageRequest).getContent());
            } else if (before != null) {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndCreatedBeforeOrCommentRoundUriInAndCommentsModifiedBefore(commentRoundUris, before, commentRoundUris, before, pageRequest).getContent());
            } else {
                return new HashSet<>(commentThreadRepository.findByCommentRoundUriIn(commentRoundUris, pageRequest).getContent());
            }
        } else if (uris != null && !uris.isEmpty()) {
            if (after != null && before != null) {
                return new HashSet<>(commentThreadRepository.findByUriInAndCreatedBetweenOrUriInAndCommentsModifiedBetween(uris, after, before, uris, after, before, pageRequest).getContent());
            } else if (after != null) {
                return new HashSet<>(commentThreadRepository.findByUriInAndCreatedAfterOrUriInAndCommentsModifiedAfter(uris, after, uris, after, pageRequest).getContent());
            } else if (before != null) {
                return new HashSet<>(commentThreadRepository.findByUriInAndCreatedBeforeOrUriInAndCommentsModifiedBefore(uris, before, uris, before, pageRequest).getContent());
            } else {
                return new HashSet<>(commentThreadRepository.findByUriIn(uris, pageRequest).getContent());
            }
        }
        return new HashSet<>(commentThreadRepository.findAll(pageRequest).getContent());
    }

    @Transactional
    public Set<CommentThread> findByCommentRoundUriIn(final Set<String> commentRoundUris,
                                                      final LocalDateTime after,
                                                      final LocalDateTime before,
                                                      final PageRequest pageRequest) {
        if (after != null && before != null) {
            return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndCreatedBetweenOrCommentRoundUriInAndCommentsModifiedBetween(commentRoundUris, after, before, commentRoundUris, after, before, pageRequest).getContent());
        } else if (after != null) {
            return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndCreatedAfterOrCommentRoundUriInAndCommentsModifiedAfter(commentRoundUris, after, commentRoundUris, after, pageRequest).getContent());
        } else if (before != null) {
            return new HashSet<>(commentThreadRepository.findByCommentRoundUriInAndCreatedBeforeOrCommentRoundUriInAndCommentsModifiedBefore(commentRoundUris, before, commentRoundUris, before, pageRequest).getContent());
        } else {
            return new HashSet<>(commentThreadRepository.findByCommentRoundUriIn(commentRoundUris, pageRequest).getContent());
        }
    }

    @Transactional
    public CommentThread findById(final UUID commentThreadId) {
        return commentThreadRepository.findById(commentThreadId);
    }

    @Transactional
    public CommentThread findByCommentRoundIdAndCommentThreadIdentifier(final UUID commentRoundId,
                                                                        final String commentThreadIdentifier) {
        final UUID commentThreadId = parseUuidFromString(commentThreadIdentifier);
        if (commentThreadId != null) {
            return findById(commentThreadId);
        } else {
            final Integer commentRoundSequenceId = parseIntegerFromString(commentThreadIdentifier);
            if (commentThreadIdentifier != null) {
                return findByCommentRoundIdAndSequenceId(commentRoundId, commentRoundSequenceId);
            }
        }
        return null;
    }

    @Transactional
    public CommentThread findByCommentRoundIdAndSequenceId(final UUID commentRoundId,
                                                           final Integer commentThreadSequenceId) {
        return commentThreadRepository.findByCommentRoundIdAndSequenceId(commentRoundId, commentThreadSequenceId);
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
    public Set<CommentThread> findByUriIn(final Set<String> uris,
                                          final LocalDateTime after,
                                          final LocalDateTime before,
                                          final PageRequest pageRequest) {
        if (after != null && before != null) {
            return new HashSet<>(commentThreadRepository.findByUriInAndCreatedBetweenOrUriInAndCommentsModifiedBetween(uris, after, before, uris, after, before, pageRequest).getContent());
        } else if (after != null) {
            return new HashSet<>(commentThreadRepository.findByUriInAndCreatedAfterOrUriInAndCommentsModifiedAfter(uris, after, uris, after, pageRequest).getContent());
        } else if (before != null) {
            return new HashSet<>(commentThreadRepository.findByUriInAndCreatedBeforeOrUriInAndCommentsModifiedBefore(uris, before, uris, before, pageRequest).getContent());
        } else {
            return new HashSet<>(commentThreadRepository.findByUriIn(uris, pageRequest).getContent());
        }
    }

    @Transactional
    public Set<CommentThread> findAll(final LocalDateTime after,
                                      final LocalDateTime before,
                                      final PageRequest pageRequest) {
        if (after != null && before != null) {
            return new HashSet<>(commentThreadRepository.findByCreatedBetweenOrCommentsModifiedBetween(after, before, after, before, pageRequest).getContent());
        } else if (after != null) {
            return new HashSet<>(commentThreadRepository.findByCreatedAfterOrCommentsModifiedAfter(after, after, pageRequest).getContent());
        } else if (before != null) {
            return new HashSet<>(commentThreadRepository.findByCreatedBeforeOrCommentsModifiedBefore(before, before, pageRequest).getContent());
        } else {
            return new HashSet<>(commentThreadRepository.findAll(pageRequest).getContent());
        }
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
            }
            saveAll(commentThreads);
            commentRound.setCommentThreads(commentThreads);
            commentRoundDao.save(commentRound);
        } else {
            saveAll(commentThreads);
        }
        return commentThreads;
    }

    @Transactional
    public int getCommentThreadCount(final Set<String> commentThreadUris,
                                     final Set<String> commentRoundUris,
                                     final LocalDateTime after,
                                     final LocalDateTime before) {
        if (commentThreadUris != null && after != null && before != null) {
            return commentThreadRepository.getCommentThreadCountWithUrisAndAfterAndBefore(commentThreadUris, after, before);
        } else if (commentThreadUris != null && after != null) {
            return commentThreadRepository.getCommentThreadCountWithUrisAndAfter(commentThreadUris, after);
        } else if (commentThreadUris != null && before != null) {
            return commentThreadRepository.getCommentThreadCountWithUrisAndBefore(commentThreadUris, before);
        } else if (commentRoundUris != null && after != null && before != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundUrisAndAfterAndBefore(commentRoundUris, after, before);
        } else if (commentRoundUris != null && after != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundIdAndAfter(commentRoundUris, after);
        } else if (commentRoundUris != null && before != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundIdAndBefore(commentRoundUris, before);
        } else if (commentRoundUris != null) {
            return commentThreadRepository.getCommentThreadCountWithCommentRoundId(commentRoundUris);
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
        final Integer sequenceId = getNextSequenceId(commentRound.getId());
        commentThread.setSequenceId(sequenceId);
        commentThread.setUri(apiUtils.createCommentThreadUri(commentRound.getSequenceId(), sequenceId));
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

    private Integer getNextSequenceId(final UUID commentRoundId) {
        final String postfix = commentRoundId.toString().replaceAll("-", "_");
        final String sequenceName = PREFIX_FOR_COMMENTTHREADS_SEQUENCE + postfix;
        return commentThreadRepository.getNextSequenceId(sequenceName);
    }
}
