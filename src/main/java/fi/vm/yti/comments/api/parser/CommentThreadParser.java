package fi.vm.yti.comments.api.parser;

import java.util.Set;

import fi.vm.yti.comments.api.dto.CommentThreadDTO;

public interface CommentThreadParser {

    CommentThreadDTO parseCommentThreadFromJson(final String jsonPayload);

    Set<CommentThreadDTO> parseCommentThreadsFromJson(final String jsonPayload);
}
