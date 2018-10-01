package fi.vm.yti.comments.api.service;

import java.util.UUID;

import fi.vm.yti.comments.api.dto.UserDTO;

public interface UserService {

    void updateUsers();

    UserDTO getUserById(final UUID id);
}
