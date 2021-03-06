package fi.vm.yti.comments.api.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.api.ApiUtils;
import fi.vm.yti.comments.api.constants.ApiConstants;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dao.OrganizationDao;
import fi.vm.yti.comments.api.dao.SourceDao;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.OrganizationDTO;
import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.entity.AbstractIdentifyableEntity;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.entity.Organization;
import fi.vm.yti.comments.api.entity.Source;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.jpa.CommentRoundRepository;
import fi.vm.yti.comments.api.security.AuthorizationManager;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_NO_RESOURCES_TO_COMMENT_STATUS_CHANGE_NOT_ALLOWED;
import static fi.vm.yti.comments.api.utils.StringUtils.parseIntegerFromString;
import static fi.vm.yti.comments.api.utils.StringUtils.parseUuidFromString;
import static java.time.LocalDateTime.now;

@Component
public class CommentRoundDaoImpl implements CommentRoundDao {

    private final CommentRoundRepository commentRoundRepository;
    private final SourceDao sourceDao;
    private final OrganizationDao organizationDao;
    private final AuthorizationManager authorizationManager;
    private final CommentThreadDao commentThreadDao;
    private final ApiUtils apiUtils;

    @Inject
    public CommentRoundDaoImpl(final CommentRoundRepository commentRoundRepository,
                               final SourceDao sourceDao,
                               final OrganizationDao organizationDao,
                               final AuthorizationManager authorizationManager,
                               final CommentThreadDao commentThreadDao,
                               final ApiUtils apiUtils) {
        this.commentRoundRepository = commentRoundRepository;
        this.sourceDao = sourceDao;
        this.organizationDao = organizationDao;
        this.authorizationManager = authorizationManager;
        this.commentThreadDao = commentThreadDao;
        this.apiUtils = apiUtils;
    }

    @Transactional
    public void save(final CommentRound commentRound) {
        commentRoundRepository.save(commentRound);
    }

    @Transactional
    public void saveAll(final Set<CommentRound> commentRounds) {
        commentRoundRepository.saveAll(commentRounds);
    }

    @Transactional
    public Set<CommentRound> findAll() {
        return commentRoundRepository.findAll();
    }

    @Transactional
    public Set<CommentRound> findAll(final PageRequest pageRequest) {
        return new HashSet<>(commentRoundRepository.findAll(pageRequest).getContent());
    }

    @Transactional
    public Set<CommentRound> findByOrganizationsIdAndStatusIn(final UUID organizationId,
                                                              final Set<String> statuses) {
        return commentRoundRepository.findByOrganizationsIdAndStatusIn(organizationId, statuses);
    }

    @Transactional
    public Set<CommentRound> findByOrganizationsId(final UUID organizationId) {
        return commentRoundRepository.findByOrganizationsId(organizationId);
    }

    @Transactional
    public Set<CommentRound> findByStatusIn(final Set<String> statuses) {
        return commentRoundRepository.findByStatusIn(statuses);
    }

    @Transactional
    public Set<CommentRound> findByStatusAndEndDateBefore(final String status,
                                                          final LocalDate now) {
        return commentRoundRepository.findByStatusAndEndDateBefore(status, now);
    }

    @Transactional
    public Set<CommentRound> findByStatusAndStartDateLessThanEqual(final String status,
                                                                   final LocalDate now) {
        return commentRoundRepository.findByStatusAndStartDateLessThanEqual(status, now);
    }

    @Transactional
    public Set<CommentRound> findBySourceContainerType(final String containerType) {
        return commentRoundRepository.findBySourceContainerType(containerType);
    }

    @Transactional
    public Set<CommentRound> findByOrganizationsIdAndSourceContainerTypeAndStatusIn(final UUID organizationId,
                                                                                    final String containerType,
                                                                                    final Set<String> statuses) {
        return commentRoundRepository.findByOrganizationsIdAndSourceContainerTypeAndStatusIn(organizationId, containerType, statuses);
    }

    @Transactional
    public Set<CommentRound> findByOrganizationsIdAndSourceContainerType(final UUID organizationId,
                                                                         final String containerType) {
        return commentRoundRepository.findByOrganizationsIdAndSourceContainerType(organizationId, containerType);
    }

    @Transactional
    public Set<CommentRound> findBySourceContainerTypeAndStatusIn(final String containerType,
                                                                  final Set<String> statuses) {
        return commentRoundRepository.findBySourceContainerTypeAndStatusIn(containerType, statuses);
    }

    @Transactional
    public CommentRound findById(final UUID commentRoundId) {
        return commentRoundRepository.findById(commentRoundId);
    }

    @Transactional
    public CommentRound findByIdentifier(final String commentRoundIdentifier) {
        final UUID commentRoundId = parseUuidFromString(commentRoundIdentifier);
        if (commentRoundId != null) {
            return findById(commentRoundId);
        } else {
            final Integer commentRoundSequenceId = parseIntegerFromString(commentRoundIdentifier);
            if (commentRoundSequenceId != null) {
                return findBySequenceId(commentRoundSequenceId);
            }
        }
        return null;
    }

    @Transactional
    public CommentRound findBySequenceId(final Integer commentRoundSequenceId) {
        return commentRoundRepository.findBySequenceId(commentRoundSequenceId);
    }

    @Transactional
    public CommentRound addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound,
                                                       final boolean removeCommentThreadOrphans) {
        final CommentRound commentRound = createOrUpdateCommentRound(fromCommentRound, removeCommentThreadOrphans);
        commentRoundRepository.save(commentRound);
        return commentRound;
    }

    @Transactional
    public Set<CommentRound> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds,
                                                              final boolean removeCommentThreadOrphans) {
        final Set<CommentRound> commentRounds = new HashSet<>();
        for (final CommentRoundDTO fromCommentRound : fromCommentRounds) {
            commentRounds.add(createOrUpdateCommentRound(fromCommentRound, removeCommentThreadOrphans));
        }
        commentRoundRepository.saveAll(commentRounds);
        return commentRounds;
    }

    @Transactional
    public void deleteCommentRound(final CommentRound commentRound) {
        commentRoundRepository.delete(commentRound);
    }

    private CommentRound createOrUpdateCommentRound(final CommentRoundDTO fromCommentRound,
                                                    final boolean removeCommentThreadOrphans) {
        final CommentRound existingCommentRound;
        if (fromCommentRound.getId() != null) {
            existingCommentRound = commentRoundRepository.findById(fromCommentRound.getId());
        } else {
            existingCommentRound = null;
        }
        final CommentRound commentRound;
        if (existingCommentRound != null) {
            commentRound = updateCommentRound(existingCommentRound, fromCommentRound, removeCommentThreadOrphans);
        } else {
            commentRound = createCommentRound(fromCommentRound);
        }
        return commentRound;
    }

    @Transactional
    public Set<CommentRound> findByUriIn(final Set<String> uris,
                                         final LocalDateTime after,
                                         final LocalDateTime before,
                                         final PageRequest pageRequest) {
        if (after != null && before != null) {
            return new HashSet<>(commentRoundRepository.findByUriInAndModifiedBetweenOrContentModifiedBetween(uris, after, before, after, before, pageRequest).getContent());
        } else if (after != null) {
            return new HashSet<>(commentRoundRepository.findByUriInAndModifiedAfterOrContentModifiedAfter(uris, after, after, pageRequest).getContent());
        } else if (before != null) {
            return new HashSet<>(commentRoundRepository.findByUriInAndModifiedBeforeOrContentModifiedBefore(uris, before, before, pageRequest).getContent());
        } else {
            return new HashSet<>(commentRoundRepository.findByUriIn(uris, pageRequest).getContent());
        }
    }

    @Transactional
    public Set<CommentRound> findAll(final LocalDateTime after,
                                     final LocalDateTime before,
                                     final PageRequest pageRequest) {
        if (after != null && before != null) {
            return new HashSet<>(commentRoundRepository.findByModifiedBetweenOrContentModifiedBetween(after, before, after, before, pageRequest).getContent());
        } else if (after != null) {
            return new HashSet<>(commentRoundRepository.findByModifiedAfterOrContentModifiedAfter(after, after, pageRequest).getContent());
        } else if (before != null) {
            return new HashSet<>(commentRoundRepository.findByModifiedBeforeOrContentModifiedBefore(before, before, pageRequest).getContent());
        } else {
            return new HashSet<>(commentRoundRepository.findAll(pageRequest).getContent());
        }
    }

    @Transactional
    public void updateContentModified(final UUID commentRoundId,
                                      final LocalDateTime timeStamp) {
        commentRoundRepository.updateContentModified(commentRoundId, timeStamp);
    }

    @Transactional
    public int getCommentRoundCount(final Set<String> commentRoundUris,
                                    final LocalDateTime after,
                                    final LocalDateTime before) {
        if (commentRoundUris != null && after != null && before != null) {
            return commentRoundRepository.getCommentRoundCountWithUrisAndAfterAndBefore(commentRoundUris, after, before);
        } else if (commentRoundUris != null && after != null) {
            return commentRoundRepository.getCommentRoundCountWithUrisAndAfter(commentRoundUris, after);
        } else if (commentRoundUris != null && before != null) {
            return commentRoundRepository.getCommentRoundCountWithUrisAndBefore(commentRoundUris, before);
        } else if (after != null && before != null) {
            return commentRoundRepository.getCommentRoundCountWithAfterAndBefore(after, before);
        } else if (after != null) {
            return commentRoundRepository.getCommentRoundCountWithAfter(after);
        } else if (before != null) {
            return commentRoundRepository.getCommentRoundCountWithBefore(before);
        } else {
            return commentRoundRepository.getCommentThreadCount();
        }
    }

    private CommentRound createCommentRound(final CommentRoundDTO fromCommentRound) {
        final CommentRound commentRound = new CommentRound();
        commentRound.setId(fromCommentRound.getId() != null ? fromCommentRound.getId() : UUID.randomUUID());
        commentRound.setUserId(authorizationManager.getUserId());
        commentRound.setLabel(fromCommentRound.getLabel());
        commentRound.setDescription(fromCommentRound.getDescription());
        commentRound.setStatus(STATUS_INCOMPLETE);
        commentRound.setStatusModified(now());
        commentRound.setOpenThreads(fromCommentRound.getOpenThreads());
        commentRound.setFixedThreads(fromCommentRound.getFixedThreads());
        commentRound.setStartDate(fromCommentRound.getStartDate());
        commentRound.setEndDate(fromCommentRound.getEndDate());
        commentRound.setSourceLocalName(fromCommentRound.getSourceLocalName());
        commentRound.setSourceLabel(fromCommentRound.getSourceLabel());
        final Integer sequenceId = commentRoundRepository.getNextSequenceId();
        commentRound.setSequenceId(sequenceId);
        commentRound.setUri(apiUtils.createCommentRoundUri(sequenceId));
        final LocalDateTime timeStamp = now();
        commentRound.setCreated(timeStamp);
        commentRound.setModified(timeStamp);
        resolveAndSetSource(commentRound, fromCommentRound);
        resolveAndSetOrganizations(commentRound, fromCommentRound);
        return commentRound;
    }

    private CommentRound updateCommentRound(final CommentRound existingCommentRound,
                                            final CommentRoundDTO fromCommentRound,
                                            final boolean removeCommentThreadOrphans) {
        final LocalDateTime timeStamp = now();
        existingCommentRound.setLabel(fromCommentRound.getLabel());
        existingCommentRound.setDescription(fromCommentRound.getDescription());
        if (!Objects.equals(existingCommentRound.getStatus(), fromCommentRound.getStatus())) {
            existingCommentRound.setStatus(fromCommentRound.getStatus());
            existingCommentRound.setStatusModified(timeStamp);
        }
        existingCommentRound.setOpenThreads(fromCommentRound.getOpenThreads());
        existingCommentRound.setFixedThreads(fromCommentRound.getFixedThreads());
        existingCommentRound.setStartDate(fromCommentRound.getStartDate());
        existingCommentRound.setEndDate(fromCommentRound.getEndDate());
        existingCommentRound.setModified(timeStamp);
        existingCommentRound.setSourceLocalName(fromCommentRound.getSourceLocalName());
        existingCommentRound.setSourceLabel(fromCommentRound.getSourceLabel());
        resolveAndSetSource(existingCommentRound, fromCommentRound);
        resolveAndSetOrganizations(existingCommentRound, fromCommentRound);
        final Set<CommentThread> commentThreads = commentThreadDao.addOrUpdateCommentThreadsFromDtos(existingCommentRound, fromCommentRound.getCommentThreads());
        if (removeCommentThreadOrphans && ApiConstants.STATUS_INCOMPLETE.equalsIgnoreCase(existingCommentRound.getStatus())) {
            final Set<String> newCommentThreadUris = commentThreads.stream().map(CommentThread::getResourceUri).collect(Collectors.toSet());
            final Set<UUID> newCommentThreadIds = commentThreads.stream().map(CommentThread::getId).collect(Collectors.toSet());
            final Set<CommentThread> existingCommentThreads = existingCommentRound.getCommentThreads();
            if (existingCommentThreads != null) {
                existingCommentThreads.forEach(existingCommentThread -> {
                    if (!newCommentThreadUris.contains(existingCommentThread.getResourceUri()) || !newCommentThreadIds.contains(existingCommentThread.getId())) {
                        existingCommentThread.setCommentRound(null);
                        commentThreadDao.delete(existingCommentThread);
                    }
                });
            }
        } else {
            if (existingCommentRound.getCommentThreads() != null) {
                final Set<UUID> newCommentThreadIds = commentThreads.stream().map(AbstractIdentifyableEntity::getId).collect(Collectors.toSet());
                final Set<CommentThread> existingCommentThreads = existingCommentRound.getCommentThreads();
                existingCommentThreads.forEach(existingCommentThread -> {
                    if (!newCommentThreadIds.contains(existingCommentRound.getId())) {
                        commentThreads.add(existingCommentThread);
                    }
                });
            }
        }
        commentThreadDao.saveAll(commentThreads);
        existingCommentRound.setCommentThreads(commentThreads);
        ensureProperStatus(existingCommentRound);
        return existingCommentRound;
    }

    private void resolveAndSetSource(final CommentRound commentRound,
                                     final CommentRoundDTO fromCommentRound) {
        final SourceDTO sourceDto = fromCommentRound.getSource();
        if (sourceDto != null && sourceDto.getContainerUri() != null) {
            final Source source = sourceDao.getOrCreateByDto(sourceDto);
            if (source != null) {
                commentRound.setSource(source);
            }
        }
    }

    private void resolveAndSetOrganizations(final CommentRound commentRound,
                                            final CommentRoundDTO fromCommentRound) {
        final Set<OrganizationDTO> organizationDtos = fromCommentRound.getOrganizations();
        final Set<Organization> organizations = new HashSet<>();
        organizationDtos.forEach(organizationDto -> {
            final Organization organization = organizationDao.findById(organizationDto.getId());
            if (organization != null) {
                organizations.add(organization);
            } else {
                throw new NotFoundException();
            }
        });
        commentRound.setOrganizations(organizations);
    }

    private void ensureProperStatus(final CommentRound commentRound) {
        final String currentStatus = commentRound.getStatus();
        if (STATUS_INPROGRESS.equalsIgnoreCase(currentStatus)) {
            if (commentRound.getCommentThreads().size() == 0) {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_NO_RESOURCES_TO_COMMENT_STATUS_CHANGE_NOT_ALLOWED));
            }
            final LocalDate now = LocalDate.now();
            final LocalDate startDate = commentRound.getStartDate();
            final LocalDate endDate = commentRound.getEndDate();
            if (startDate != null && startDate.isAfter(now)) {
                commentRound.setStatus(STATUS_AWAIT);
            }
            if (endDate != null && endDate.isBefore(LocalDate.now())) {
                commentRound.setStatus(STATUS_ENDED);
            }
        }
    }
}
