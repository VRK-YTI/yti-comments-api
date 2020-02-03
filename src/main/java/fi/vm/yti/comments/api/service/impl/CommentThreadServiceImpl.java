package fi.vm.yti.comments.api.service.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.dto.DtoMapperService;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.service.CommentThreadService;

@Component
public class CommentThreadServiceImpl extends AbstractService implements CommentThreadService {

    private final DtoMapperService dtoMapperService;
    private final CommentThreadDao commentThreadDao;
    private final CommentRoundDao commentRoundDao;

    public CommentThreadServiceImpl(final DtoMapperService dtoMapperService,
                                    final CommentThreadDao commentThreadDao,
                                    final CommentRoundDao commentRoundDao) {
        this.dtoMapperService = dtoMapperService;
        this.commentThreadDao = commentThreadDao;
        this.commentRoundDao = commentRoundDao;
    }

    @Transactional
    public Set<CommentThreadDTO> findAll() {
        return dtoMapperService.mapDeepCommentThreads(commentThreadDao.findAll());
    }

    @Override
    public Set<CommentThreadDTO> findAll(final PageRequest pageable) {
        return dtoMapperService.mapDeepCommentThreads(commentThreadDao.findAll(pageable));
    }

    @Transactional
    public CommentThreadDTO findById(final UUID commentThreadId) {
        return dtoMapperService.mapDeepCommentThread(commentThreadDao.findById(commentThreadId));
    }

    @Transactional
    public CommentThreadDTO findByCommentRoundIdAndCommentThreadIdentifier(final UUID commentRoundId,
                                                                           final String commentThreadIdentifier) {
        return dtoMapperService.mapDeepCommentThread(commentThreadDao.findByCommentRoundIdAndCommentThreadIdentifier(commentRoundId, commentThreadIdentifier));
    }

    @Transactional
    public Set<CommentThreadDTO> findByCommentRoundId(final UUID commentRoundId) {
        return dtoMapperService.mapDeepCommentThreads(commentThreadDao.findByCommentRoundId(commentRoundId));
    }

    @Transactional
    public CommentThreadDTO addOrUpdateCommentThreadFromDto(final UUID commentRoundId,
                                                            final CommentThreadDTO fromCommentThread) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            return dtoMapperService.mapDeepCommentThread(commentThreadDao.addOrUpdateCommentThreadFromDto(commentRound, fromCommentThread));
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional
    public Set<CommentThreadDTO> addOrUpdateCommentThreadsFromDtos(final UUID commentRoundId,
                                                                   final Set<CommentThreadDTO> fromCommentThreads,
                                                                   final boolean removeOrphans) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            return dtoMapperService.mapDeepCommentThreads(commentThreadDao.addOrUpdateCommentThreadsFromDtos(commentRound, fromCommentThreads, removeOrphans));
        } else {
            throw new NotFoundException();
        }
    }

    @Transactional
    public void deleteCommentThread(final CommentThread commentThread) {
        commentThreadDao.deleteCommentThread(commentThread);
    }

    @Transactional
    public int getCommentThreadCount(final Set<String> commentThreadUris,
                                     final Set<String> containerUris,
                                     final LocalDateTime after,
                                     final LocalDateTime before) {
        return commentThreadDao.getCommentThreadCount(commentThreadUris, containerUris, after, before);
    }

    @Transactional
    public Set<ResourceDTO> getResources(final Set<String> commentThreadUris,
                                         final Set<String> containerUris,
                                         final Meta meta) {
        final LocalDateTime after = convertDateToLocalDateTime(meta.getAfter());
        final LocalDateTime before = convertDateToLocalDateTime(meta.getBefore());
        final int commentThreadCount = getCommentThreadCount(commentThreadUris, containerUris, after, before);
        meta.setTotalResults(commentThreadCount);
        final int page = getPageIndex(meta);
        final int pageSize = meta.getPageSize() != null ? meta.getPageSize() : MAX_PAGE_SIZE;
        final PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, FIELD_SEQUENCE_ID));
        if (containerUris != null && !containerUris.isEmpty() && commentThreadUris != null && !commentThreadUris.isEmpty()) {
            return dtoMapperService.mapCommentThreadsToResources(commentThreadDao.findByCommentRoundUriInAndUriIn(containerUris, commentThreadUris, after, before, pageRequest));
        } else if (containerUris != null && !containerUris.isEmpty()) {
            return dtoMapperService.mapCommentThreadsToResources(commentThreadDao.findByCommentRoundUriIn(containerUris, after, before, pageRequest));
        } else if (commentThreadUris != null && !commentThreadUris.isEmpty()) {
            return dtoMapperService.mapCommentThreadsToResources(commentThreadDao.findByUriIn(commentThreadUris, after, before, pageRequest));
        } else {
            return dtoMapperService.mapCommentThreadsToResources(commentThreadDao.findAll(after, before, pageRequest));
        }
    }
}
