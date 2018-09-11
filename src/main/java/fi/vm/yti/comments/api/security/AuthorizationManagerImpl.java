package fi.vm.yti.comments.api.security;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import fi.vm.yti.security.AuthenticatedUserProvider;

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
}
