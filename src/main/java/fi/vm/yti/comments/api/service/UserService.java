package fi.vm.yti.comments.api.service;

import java.util.Set;
import java.util.UUID;

import fi.vm.yti.comments.api.dto.UserDTO;

public interface UserService {

    void updateUsers();

    void updateTempUsers();

    UserDTO getUserById(final UUID id);

    String getUserEmailById(final UUID id);

    Set<UserDTO> getUsersByCommentRoundUri(final String uri);
}
