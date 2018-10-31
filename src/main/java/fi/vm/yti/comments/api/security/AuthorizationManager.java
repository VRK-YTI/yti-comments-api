package fi.vm.yti.comments.api.security;

import java.util.UUID;

import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;

public interface AuthorizationManager {

    boolean isSuperUser();

    UUID getUserId();

    boolean canUserModifyComment(final Comment comment);

    boolean canUserAddCommentRound();

    boolean canUserModifyCommentRound(final CommentRound commentRound);

    boolean canUserAddCommentsToCommentRound(final CommentRound commentRound);

    boolean canUserAddCommentThreadsToCommentRound(final CommentRound commentRound);

}
