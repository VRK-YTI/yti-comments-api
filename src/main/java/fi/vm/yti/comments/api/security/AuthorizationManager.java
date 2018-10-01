package fi.vm.yti.comments.api.security;

import java.util.UUID;

public interface AuthorizationManager {

    boolean isSuperUser();

    UUID getUserId();

}
