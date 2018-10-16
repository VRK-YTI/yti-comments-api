package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentDTO;

public interface CommentService {

    Set<CommentDTO> findAll();

    CommentDTO findById(final UUID commentId);

    Set<CommentDTO> findCommentRoundMainLevelCommentsForUserId(final UUID commentRoundId,
                                                               final UUID userId);

    Set<CommentDTO> findByCommentThreadId(final UUID commentThreadId);

    CommentDTO addOrUpdateCommentFromDto(final UUID commentRoundId,
                                         final UUID commentThreadId,
                                         final CommentDTO fromComment);

    Set<CommentDTO> addOrUpdateCommentsFromDtos(final UUID commentRoundId,
                                                final Set<CommentDTO> fromComments);

    Set<CommentDTO> addOrUpdateCommentsFromDtos(final UUID commentRoundId,
                                                final UUID commentThreadId,
                                                final Set<CommentDTO> fromComments);
}
