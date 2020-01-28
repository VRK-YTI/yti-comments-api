package fi.vm.yti.comments.api.security;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;

public interface AuthorizationManager {

    boolean isSuperUser();

    UUID getUserId();

    String getUserEmail();

    String getContainerUri();

    Set<UUID> getUserOrganizations();

    boolean canUserModifyComment(final Comment comment);

    boolean canUserDeleteComment(final Comment comment);

    boolean canUserAddCommentRound();

    boolean canUserDeleteCommentRound(final CommentRound commentRound);

    boolean canUserDeleteCommentThread(final CommentRound commentRound);

    boolean canUserModifyCommentRound(final CommentRound commentRound);

    boolean canUserAddCommentsToCommentRound(final CommentRound commentRound);

    boolean canUserAddCommentThreadsToCommentRound(final CommentRound commentRound);

}
