package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentRoundDTO;

public interface CommentRoundService {

    Set<CommentRoundDTO> findAll();

    CommentRoundDTO findById(final UUID commentRoundId);

    CommentRoundDTO addOrUpdateCommentRoundFromDto(final CommentRoundDTO fromCommentRound);

    Set<CommentRoundDTO> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> fromCommentRounds);
}
