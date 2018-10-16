package fi.vm.yti.comments.api.security;

import java.util.UUID;

import fi.vm.yti.comments.api.entity.CommentRound;

public interface AuthorizationManager {

    boolean isSuperUser();

    UUID getUserId();

    boolean canUserAddCommentsToCommentRound(final CommentRound commentRound);

    boolean canUserAddCommentThreadsToCommentRound(final CommentRound commentRound);

}
