package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentDTO;

public interface CommentService {

    Set<CommentDTO> findAll();

    CommentDTO findById(final UUID commentId);

    Set<CommentDTO> findByGlobalCommentsId(final UUID globalComments);

    Set<CommentDTO> findByCommentRoundId(final UUID commentRound);

    CommentDTO addOrUpdateCommentFromDto(final CommentDTO fromComment);

    Set<CommentDTO> addOrUpdateCommentsFromDtos(final Set<CommentDTO> fromComments);
}
