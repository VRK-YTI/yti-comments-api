package fi.vm.yti.comments.api.security;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import fi.vm.yti.comments.api.entity.AbstractIdentifyableEntity;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.service.UserService;
import fi.vm.yti.security.AuthenticatedUserProvider;
import fi.vm.yti.security.Role;
import fi.vm.yti.security.YtiUser;
import static fi.vm.yti.comments.api.constants.ApiConstants.STATUS_INCOMPLETE;
import static fi.vm.yti.comments.api.constants.ApiConstants.STATUS_INPROGRESS;
import static fi.vm.yti.security.Role.*;

@Service
public class AuthorizationManagerImpl implements AuthorizationManager {

    private final AuthenticatedUserProvider userProvider;
    private final UserService userService;
    private static final String TOKEN_ROLE_MEMBER = "MEMBER";

    @Inject
    AuthorizationManagerImpl(final AuthenticatedUserProvider userProvider,
                             final UserService userService) {
        this.userProvider = userProvider;
        this.userService = userService;
    }

    public boolean isSuperUser() {
        return userProvider.getUser().isSuperuser();
    }

    public UUID getUserId() {
        return userProvider.getUser().getId();
    }

    public String getContainerUri() {
        return userProvider.getUser().getContainerUri();
    }

    public String getUserEmail() {
        return userProvider.getUser().getEmail();
    }

    public Set<UUID> getUserOrganizations() {
        final Set<Role> roles = new HashSet<>();
        roles.add(ADMIN);
        roles.add(DATA_MODEL_EDITOR);
        roles.add(TERMINOLOGY_EDITOR);
        roles.add(CODE_LIST_EDITOR);
        roles.add(MEMBER);
        return userProvider.getUser().getOrganizations(roles);
    }

    public boolean canUserModifyComment(final Comment comment) {
        final YtiUser user = userProvider.getUser();
        return user.getId().equals(comment.getUserId());
    }

    public boolean canUserDeleteComment(final Comment comment) {
        final YtiUser user = userProvider.getUser();
        return user.getId().equals(comment.getUserId());
    }

    public boolean canUserAddCommentRound() {
        final YtiUser user = userProvider.getUser();
        return user.isSuperuser() || (user.isInRoleInAnyOrganization(ADMIN) || user.isInRoleInAnyOrganization(CODE_LIST_EDITOR) || user.isInRoleInAnyOrganization(TERMINOLOGY_EDITOR) || user.isInRoleInAnyOrganization(DATA_MODEL_EDITOR));
    }

    public boolean canUserDeleteCommentRound(final CommentRound commentRound) {
        final YtiUser user = userProvider.getUser();
        return user.isSuperuser() || (commentRound.getUserId() == user.getId());
    }

    public boolean canUserDeleteCommentThread(final CommentRound commentRound) {
        final YtiUser user = userProvider.getUser();
        return user.isSuperuser() || (commentRound.getUserId() == user.getId() && commentRound.getStatus().equalsIgnoreCase(STATUS_INPROGRESS));
    }

    public boolean canUserModifyCommentRound(final CommentRound commentRound) {
        final YtiUser user = userProvider.getUser();
        return user.isSuperuser() || commentRound.getUserId() == user.getId();
    }

    public boolean canUserAddCommentsToCommentRound(final CommentRound commentRound) {
        final YtiUser user = userProvider.getUser();
        final Collection<UUID> organizationIds = commentRound.getOrganizations().stream().map(AbstractIdentifyableEntity::getId).collect(Collectors.toList());
        if (STATUS_INPROGRESS.equalsIgnoreCase(commentRound.getStatus())) {
            if (user.isSuperuser()) {
                return true;
            } else if (user.getId().equals(commentRound.getUserId())) {
                return true;
            } else if (user.isInAnyRole(EnumSet.of(ADMIN, CODE_LIST_EDITOR, TERMINOLOGY_EDITOR, DATA_MODEL_EDITOR, MEMBER), organizationIds)) {
                return true;
            } else if (user.getTokenRole() != null && TOKEN_ROLE_MEMBER.equalsIgnoreCase(user.getTokenRole()) && user.getContainerUri() != null && user.getContainerUri().equalsIgnoreCase(commentRound.getUri())) {
                return true;
            }
        }
        return false;
    }

    public boolean canUserAddCommentThreadsToCommentRound(final CommentRound commentRound) {
        final YtiUser user = userProvider.getUser();
        final Collection<UUID> organizationIds = commentRound.getOrganizations().stream().map(AbstractIdentifyableEntity::getId).collect(Collectors.toList());
        if (STATUS_INCOMPLETE.equalsIgnoreCase(commentRound.getStatus())) {
            if (user.isSuperuser()) {
                return true;
            } else if (user.getId().equals(commentRound.getUserId())) {
                return true;
            }
        }
        if (STATUS_INPROGRESS.equalsIgnoreCase(commentRound.getStatus()) && !commentRound.getFixedThreads()) {
            if (user.isSuperuser()) {
                return true;
            } else if (user.getId().equals(commentRound.getUserId())) {
                return true;
            } else if (user.isInAnyRole(EnumSet.of(ADMIN, CODE_LIST_EDITOR, TERMINOLOGY_EDITOR, DATA_MODEL_EDITOR, MEMBER), organizationIds)) {
                return true;
            } else if (user.getTokenRole() != null && TOKEN_ROLE_MEMBER.equalsIgnoreCase(user.getTokenRole()) && user.getContainerUri() != null && user.getContainerUri().equalsIgnoreCase(commentRound.getUri())) {
                return true;
            }
        }
        return false;
    }
}
