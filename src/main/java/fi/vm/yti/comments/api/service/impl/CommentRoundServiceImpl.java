package fi.vm.yti.comments.api.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.DtoMapperService;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.service.CommentRoundService;
import fi.vm.yti.comments.api.service.GroupmanagementProxyService;

@Component
public class CommentRoundServiceImpl extends AbstractService implements CommentRoundService {

    private final CommentRoundDao commentRoundDao;
    private final DtoMapperService dtoMapperService;
    private final GroupmanagementProxyService groupmanagementProxyService;

    public CommentRoundServiceImpl(final CommentRoundDao commentRoundDao,
                                   final DtoMapperService dtoMapperService,
                                   final GroupmanagementProxyService groupmanagementProxyService) {
        this.commentRoundDao = commentRoundDao;
        this.dtoMapperService = dtoMapperService;
        this.groupmanagementProxyService = groupmanagementProxyService;
    }

    @Transactional
    public Set<CommentRoundDTO> findAll() {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findAll());
    }

    @Override
    public Set<CommentRoundDTO> findAll(final PageRequest pageable) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findAll(pageable));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsIdAndStatus(final UUID organizationId,
                                                               final String status) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findByOrganizationsIdAndStatus(organizationId, status));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsId(final UUID organizationId) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findByOrganizationsId(organizationId));
    }

    @Transactional
    public Set<CommentRoundDTO> findByStatus(final String status) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findByStatus(status));
    }

    @Transactional
    public Set<CommentRoundDTO> findBySourceContainerType(final String containerType) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findBySourceContainerType(containerType));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsIdAndStatusAndSourceContainerType(final UUID organizationId,
                                                                                     final String status,
                                                                                     final String containerType) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findByOrganizationsIdAndStatusAndSourceContainerType(organizationId, status, containerType));
    }

    @Transactional
    public Set<CommentRoundDTO> findByOrganizationsIdAndSourceContainerType(final UUID organizationId,
                                                                            final String containerType) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findByOrganizationsIdAndSourceContainerType(organizationId, containerType));
    }

    @Transactional
    public Set<CommentRoundDTO> findByStatusAndSourceContainerType(final String status,
                                                                   final String containerType) {
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.findByStatusAndSourceContainerType(status, containerType));
    }

    @Transactional
    public CommentRoundDTO findById(final UUID commentRoundId) {
        return dtoMapperService.mapDeepCommentRound(commentRoundDao.findById(commentRoundId));
    }

    @Transactional
    public CommentRoundDTO findByIdentifier(final String commentRoundIdentifier) {
        return dtoMapperService.mapDeepCommentRound(commentRoundDao.findByIdentifier(commentRoundIdentifier));
    }

    @Transactional
    public CommentRoundDTO addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound,
                                                          final boolean removeCommentThreadOrphans) {
        addOrUpdateGroupmanagementTempUsers(fromCommentRound);
        return dtoMapperService.mapDeepCommentRound(commentRoundDao.addOrUpdateCommentRoundFromDto(fromCommentRound, removeCommentThreadOrphans));
    }

    @Transactional
    public Set<CommentRoundDTO> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds,
                                                                 final boolean removeCommentThreadOrphans) {
        fromCommentRounds.forEach(this::addOrUpdateGroupmanagementTempUsers);
        return dtoMapperService.mapDeepCommentRounds(commentRoundDao.addOrUpdateCommentRoundsFromDtos(fromCommentRounds, removeCommentThreadOrphans));
    }

    private void addOrUpdateGroupmanagementTempUsers(final CommentRoundDTO commentRoundDto) {
        if (commentRoundDto.getUri() != null) {
            groupmanagementProxyService.addOrUpdateTempUsers(commentRoundDto.getUri(), commentRoundDto.getTempUsers());
        }
    }

    @Transactional
    public void deleteCommentRound(final CommentRound commentRound) {
        groupmanagementProxyService.addOrUpdateTempUsers(commentRound.getUri(), new HashSet<>());
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
        final PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, FIELD_SEQUENCE_ID));
        if (commentRoundUris != null && !commentRoundUris.isEmpty()) {
            return dtoMapperService.mapCommentRoundsToResources(commentRoundDao.findByUriIn(commentRoundUris, after, before, pageRequest));
        } else {
            return dtoMapperService.mapCommentRoundsToResources(commentRoundDao.findAll(after, before, pageRequest));
        }
    }
}
