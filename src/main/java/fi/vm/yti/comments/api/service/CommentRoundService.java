package fi.vm.yti.comments.api.service;

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

    Set<CommentRoundDTO> findByOrganizationsIdAndStatus(final UUID organizationId,
                                                        final String status,
                                                        final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByOrganizationsId(final UUID organizationId,
                                               final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByStatus(final String status,
                                      final boolean includeCommentThreads);

    Set<CommentRoundDTO> findBySourceContainerType(final String containerType,
                                                   final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByOrganizationsIdAndStatusAndSourceContainerType(final UUID organizationId,
                                                                              final String status,
                                                                              final String containerType,
                                                                              final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByOrganizationsIdAndSourceContainerType(final UUID organizationId,
                                                                     final String containerType,
                                                                     final boolean includeCommentThreads);

    Set<CommentRoundDTO> findByStatusAndSourceContainerType(final String status,
                                                            final String containerType,
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
