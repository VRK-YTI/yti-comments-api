package fi.vm.yti.comments.api.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;

import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.error.Meta;

public interface CommentRoundService {

    Set<CommentRoundDTO> findAll(final boolean includeCommentThreads);

    Set<CommentRoundDTO> findAll(final PageRequest pageable);

    Set<CommentRoundDTO> findByOrganizationsIdAndStatusIn(final UUID organizationId,
                                                          final Set<String> statuses,
                                                          final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByOrganizationsId(final UUID organizationId,
                                               final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByStatusIn(final Set<String> statuses,
                                        final boolean includeCommentThreads);

    Set<CommentRoundDTO> findBySourceContainerType(final String containerType,
                                                   final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByOrganizationsIdAndSourceContainerTypeAndStatusIn(final UUID organizationId,
                                                                                final String containerType,
                                                                                final Set<String> statuses,
                                                                                final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByOrganizationsIdAndSourceContainerType(final UUID organizationId,
                                                                     final String containerType,
                                                                     final boolean includeCommentThreads);

    Set<CommentRoundDTO> findBySourceContainerTypeAndStatusIn(final String containerType,
                                                              final Set<String> statuses,
                                                              final boolean includeCommentThreads);

    CommentRoundDTO findById(final UUID commentRoundId,
                             final boolean includeCommentThreads);

    CommentRoundDTO findByIdentifier(final String commentRoundIdentifier,
                                     final boolean includeCommentThreads);

    CommentRoundDTO addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound,
                                                   final boolean removeCommentThreadOrphans);

    Set<CommentRoundDTO> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds,
                                                          final boolean removeCommentThreadOrphans);

    void deleteCommentRound(final CommentRound commentRound);

    Set<ResourceDTO> getContainers(final Set<String> commentRoundUris,
                                   final Meta meta);
}
