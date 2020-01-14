package fi.vm.yti.comments.api.service;

import java.util.Set;

import fi.vm.yti.comments.api.dto.UserDTO;

public interface GroupmanagementProxyService {

    void addOrUpdateTempUsers(final String containerUri,
                              final Set<UserDTO> tempUsers);
}
