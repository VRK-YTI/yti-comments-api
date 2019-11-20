package fi.vm.yti.comments.api.service.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.DtoMapper;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.service.CommentRoundService;

@Component
public class CommentRoundServiceImpl extends AbstractService implements CommentRoundService {

    private final CommentRoundDao commentRoundDao;
    private final DtoMapper dtoMapper;

    public CommentRoundServiceImpl(final CommentRoundDao commentRoundDao,
                                   final DtoMapper dtoMapper) {
        this.commentRoundDao = commentRoundDao;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Set<CommentRoundDTO> findAll() {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findAll());
    }

    @Override
    public Set<CommentRoundDTO> findAll(final PageRequest pageable) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findAll(pageable));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsIdAndStatus(final UUID organizationId,
                                                               final String status) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findByOrganizationsIdAndStatus(organizationId, status));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsId(final UUID organizationId) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findByOrganizationsId(organizationId));
    }

    @Transactional
    public Set<CommentRoundDTO> findByStatus(final String status) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findByStatus(status));
    }

    @Transactional
    public Set<CommentRoundDTO> findBySourceContainerType(final String containerType) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findBySourceContainerType(containerType));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsIdAndStatusAndSourceContainerType(final UUID organizationId,
                                                                                     final String status,
                                                                                     final String containerType) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findByOrganizationsIdAndStatusAndSourceContainerType(organizationId, status, containerType));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsIdAndSourceContainerType(final UUID organizationId,
                                                                            final String containerType) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findByOrganizationsIdAndSourceContainerType(organizationId, containerType));
    }

    @Transactional
    public Set<CommentRoundDTO> findByStatusAndSourceContainerType(final String status,
                                                                   final String containerType) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.findByStatusAndSourceContainerType(status, containerType));
    }

    @Transactional
    public CommentRoundDTO findById(final UUID commentRoundId) {
        return dtoMapper.mapDeepCommentRound(commentRoundDao.findById(commentRoundId));
    }

    @Transactional
    public CommentRoundDTO findByIdentifier(final String commentRoundIdentifier) {
        return dtoMapper.mapDeepCommentRound(commentRoundDao.findByIdentifier(commentRoundIdentifier));
    }

    @Transactional
    public CommentRoundDTO addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound,
                                                          final boolean removeCommentThreadOrphans) {
        return dtoMapper.mapDeepCommentRound(commentRoundDao.addOrUpdateCommentRoundFromDto(fromCommentRound, removeCommentThreadOrphans));
    }

    @Transactional
    public Set<CommentRoundDTO> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds,
                                                                 final boolean removeCommentThreadOrphans) {
        return dtoMapper.mapDeepCommentRounds(commentRoundDao.addOrUpdateCommentRoundsFromDtos(fromCommentRounds, removeCommentThreadOrphans));
    }

    @Transactional
    public void deleteCommentRound(final CommentRound commentRound) {
        commentRoundDao.deleteCommentRound(commentRound);
    }

    @Transactional
    public int getCommentRoundCount(final Set<String> commentRoundUris,
                                    final LocalDateTime after,
                                    final LocalDateTime before) {
        return commentRoundDao.getCommentRoundCount(commentRoundUris, after, before);
    }

    @Transactional
    public Set<ResourceDTO> getContainers(final Set<String> commentRoundUris,
                                          final Meta meta) {
        final LocalDateTime after = convertDateToLocalDateTime(meta.getAfter());
        final LocalDateTime before = convertDateToLocalDateTime(meta.getBefore());
        final int commentRoundCount = getCommentRoundCount(commentRoundUris, after, before);
        meta.setTotalResults(commentRoundCount);
        int page = getPageIndex(meta);
        final int pageSize = meta.getPageSize() != null ? meta.getPageSize() : MAX_PAGE_SIZE;
        final PageRequest pageRequest = PageRequest.of(page, pageSize, new Sort(Sort.Direction.ASC, FIELD_SEQUENCE_ID));
        if (commentRoundUris != null && !commentRoundUris.isEmpty()) {
            return dtoMapper.mapCommentRoundsToResources(commentRoundDao.findByUriIn(commentRoundUris, after, before, pageRequest));
        } else {
            return dtoMapper.mapCommentRoundsToResources(commentRoundDao.findAll(pageRequest));
        }
    }
}
