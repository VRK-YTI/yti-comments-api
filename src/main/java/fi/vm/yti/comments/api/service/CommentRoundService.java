package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;

import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.error.Meta;

public interface CommentRoundService {

    Set<CommentRoundDTO> findAll();

    Set<CommentRoundDTO> findAll(final PageRequest pageable);

    Set<CommentRoundDTO> findByOrganizationsIdAndStatus(final UUID organizationId,
                                                        final String status);

    Set<CommentRoundDTO> findByOrganizationsId(final UUID organizationId);

    Set<CommentRoundDTO> findByStatus(final String status);

    Set<CommentRoundDTO> findBySourceContainerType(final String containerType);

    Set<CommentRoundDTO> findByOrganizationsIdAndStatusAndSourceContainerType(final UUID organizationId,
                                                                              final String status,
                                                                              final String containerType);

    Set<CommentRoundDTO> findByOrganizationsIdAndSourceContainerType(final UUID organizationId,
                                                                     final String containerType);

    Set<CommentRoundDTO> findByStatusAndSourceContainerType(final String status,
                                                            final String containerType);

    CommentRoundDTO findById(final UUID commentRoundId);

    CommentRoundDTO findByIdentifier(final String commentRoundIdentifier);

    CommentRoundDTO addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound,
                                                   final boolean removeCommentThreadOrphans);

    Set<CommentRoundDTO> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds,
                                                          final boolean removeCommentThreadOrphans);

    void deleteCommentRound(final CommentRound commentRound);

    Set<ResourceDTO> getContainers(final Set<String> commentRoundUris,
                                   final Meta meta);
}
