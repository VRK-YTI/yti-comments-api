package fi.vm.yti.comments.api.parser;

import java.util.Set;

import fi.vm.yti.comments.api.dto.CommentRoundDTO;

public interface CommentRoundParser {

    CommentRoundDTO parseCommentRoundFromJson(final String jsonPayload);

    Set<CommentRoundDTO> parseCommentRoundsFromJson(final String jsonPayload);
}
