package fi.vm.yti.comments.api.parser;

import java.util.Set;

import fi.vm.yti.comments.api.dto.CommentRoundGroupDTO;

public interface CommentRoundGroupParser {

    CommentRoundGroupDTO parseCommentRoundGroupFromJson(final String jsonPayload);

    Set<CommentRoundGroupDTO> parseCommentRoundGroupsFromJson(final String jsonPayload);
}
