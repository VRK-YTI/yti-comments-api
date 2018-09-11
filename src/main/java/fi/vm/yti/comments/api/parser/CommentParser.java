package fi.vm.yti.comments.api.parser;

import java.util.Set;

import fi.vm.yti.comments.api.dto.CommentDTO;

public interface CommentParser {

    CommentDTO parseCommentFromJson(final String jsonPayload);

    Set<CommentDTO> parseCommentsFromJson(final String jsonPayload);
}
