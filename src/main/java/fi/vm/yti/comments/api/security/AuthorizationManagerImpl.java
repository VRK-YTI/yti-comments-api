package fi.vm.yti.comments.api.security;

import java.util.Collection;
import java.util.EnumSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import fi.vm.yti.comments.api.entity.AbstractIdentifyableEntity;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.security.AuthenticatedUserProvider;
import fi.vm.yti.security.YtiUser;
import static fi.vm.yti.security.Role.*;

@Service
public class AuthorizationManagerImpl implements AuthorizationManager {

    private final AuthenticatedUserProvider userProvider;

    @Inject
    AuthorizationManagerImpl(final AuthenticatedUserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public boolean isSuperUser() {
        return userProvider.getUser().isSuperuser();
    }

    public UUID getUserId() {
        return userProvider.getUser().getId();
    }

    public boolean canUserModifyComment(final Comment comment) {
        final YtiUser user = userProvider.getUser();
        return user.getId() == comment.getUserId();
    }

    public boolean canUserAddCommentsToCommentRound(final CommentRound commentRound) {
        final YtiUser user = userProvider.getUser();
        final Collection<UUID> organizationIds = commentRound.getOrganizations().stream().map(AbstractIdentifyableEntity::getId).collect(Collectors.toList());
        return user.isSuperuser() || (user.isInAnyRole(EnumSet.of(ADMIN, CODE_LIST_EDITOR, TERMINOLOGY_EDITOR, DATA_MODEL_EDITOR), organizationIds) && "INPROGRESS".equalsIgnoreCase(commentRound.getStatus()));
    }

    public boolean canUserAddCommentThreadsToCommentRound(final CommentRound commentRound) {
        final YtiUser user = userProvider.getUser();
        final Collection<UUID> organizationIds = commentRound.getOrganizations().stream().map(AbstractIdentifyableEntity::getId).collect(Collectors.toList());
        return user.isSuperuser() ||
            (user.getId() == commentRound.getUserId() && "AWAIT".equalsIgnoreCase(commentRound.getStatus())) ||
            ((user.getId() == commentRound.getUserId() || user.isInAnyRole(EnumSet.of(ADMIN, CODE_LIST_EDITOR, TERMINOLOGY_EDITOR, DATA_MODEL_EDITOR), organizationIds)) &&
            ("INPROGRESS".equalsIgnoreCase(commentRound.getStatus()) && !commentRound.getFixedThreads()));
    }
}
