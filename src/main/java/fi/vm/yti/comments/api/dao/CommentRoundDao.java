package fi.vm.yti.comments.api.dao;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.entity.CommentRound;

public interface CommentRoundDao {

    Set<CommentRound> findAll();

    CommentRound findById(final UUID commentRoundId);

    CommentRound addOrUpdateCommentRoundFromDto(final CommentRoundDTO commentRoundDto);

    Set<CommentRound> addOrUpdateCommentRoundsFromDtos(final Set<CommentRoundDTO> commentRoundDtos);
}
