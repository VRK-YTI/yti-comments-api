package fi.vm.yti.comments.api.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.configuration.GroupManagementProperties;
import fi.vm.yti.comments.api.dto.UserDTO;
import fi.vm.yti.comments.api.service.UserService;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static org.springframework.http.HttpMethod.GET;

@Component
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final Map<UUID, UserDTO> users;
    private final Map<UUID, UserDTO> tempUsers;
    private final GroupManagementProperties groupManagementProperties;
    private final RestTemplate restTemplate;

    @Inject
    public UserServiceImpl(final GroupManagementProperties groupManagementProperties,
                           final RestTemplate restTemplate) {
        this.groupManagementProperties = groupManagementProperties;
        this.restTemplate = restTemplate;
        users = new HashMap<>();
        tempUsers = new HashMap<>();
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void fetchUsers() {
        updateUsers();
    }

    public void updateUsers() {
        updateRegularUsers();
        updateTempUsers();
    }

    private void updateRegularUsers() {
        final String url = groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH + "/" + GROUPMANAGEMENT_API_USERS;
        LOG.debug("Updating users from Groupmanagement URL: " + url);
        final Set<UserDTO> fetchedUsers = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<Set<UserDTO>>() {
        }).getBody();
        if (fetchedUsers != null) {
            LOG.info(String.format("Successfully synced %d users from groupmanagement service!", fetchedUsers.size()));
        }
        Objects.requireNonNull(fetchedUsers).forEach(user -> users.put(user.getId(), user));
    }

    public void updateTempUsers() {
        final String url = groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH + "/" + GROUPMANAGEMENT_API_TEMPUSERS;
        LOG.debug("Updating temp users from Groupmanagement URL: " + url);
        final Set<UserDTO> fetchedUsers = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<Set<UserDTO>>() {
        }).getBody();
        if (fetchedUsers != null) {
            LOG.info(String.format("Successfully synced %d temp users from groupmanagement service!", fetchedUsers.size()));
        }
        Objects.requireNonNull(fetchedUsers).forEach(user -> {
            tempUsers.put(user.getId(), user);
        });
    }

    public UserDTO getUserById(final UUID id) {
        final UserDTO user = users.get(id);
        if (user == null) {
            return tempUsers.get(id);
        }
        return user;
    }

    public String getUserEmailById(final UUID id) {
        return users.get(id).getEmail();
    }

    public Set<UserDTO> getUsersByCommentRoundUri(final String uri) {
        final Set<UserDTO> roundUsers = new HashSet<>();
        for (final UserDTO user : tempUsers.values()) {
            if (uri.equalsIgnoreCase(user.getContainerUri()) && user.getEmail() != null) {
                roundUsers.add(user);
            }
        }
        return roundUsers;
    }
}
