package fi.vm.yti.comments.api.security;

import java.util.UUID;

interface AuthorizationManager {

    boolean isSuperUser();

    UUID getUserId();

}
