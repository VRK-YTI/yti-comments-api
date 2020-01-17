package fi.vm.yti.comments.api.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import fi.vm.yti.comments.api.configuration.GroupManagementProperties;
import fi.vm.yti.comments.api.dto.UserDTO;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.ErrorConstants;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.service.GroupmanagementProxyService;
import fi.vm.yti.comments.api.service.UserService;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Service
public class GroupmanagementProxyServiceImpl implements GroupmanagementProxyService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupmanagementProxyServiceImpl.class);

    private final GroupManagementProperties groupManagementProperties;
    private final RestTemplate restTemplate;
    private final UserService userService;

    public GroupmanagementProxyServiceImpl(final GroupManagementProperties groupManagementProperties,
                                           final RestTemplate restTemplate,
                                           final UserService userService) {
        this.groupManagementProperties = groupManagementProperties;
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    public void addOrUpdateTempUsers(final String containerUri,
                                     final Set<UserDTO> tempUsers) {
        final String requestUrl = createGroupManagementTempUsersApiUrl(containerUri);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        final HttpEntity<String> request;
        try {
            if (tempUsers != null) {
                request = new HttpEntity<>(mapper.writeValueAsString(tempUsers), headers);
            } else {
                request = new HttpEntity<>(headers);
            }
        } catch (final JsonProcessingException e) {
            LOG.error("Cannot map tempUsers for addOrUpdateTempUsers request.", e);
            throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorConstants.ERR_MSG_USER_500));
        }
        final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.POST, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                final Object responseBody = response.getBody();
                if (responseBody != null) {
                    final Set<UserDTO> addedTempUsers = mapper.readValue(responseBody.toString(), new TypeReference<Set<UserDTO>>() {
                    });
                    if (addedTempUsers != null) {
                        LOG.debug("Added or updated " + addedTempUsers.size() + " temporary users to groupmanagement.");
                    }
                } else {
                    LOG.error("Parsing tempUsers from addOrUpdateTempUsers to groupmanagement failed!");
                    throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorConstants.ERR_MSG_USER_500));
                }
            } catch (final IOException e) {
                LOG.error("Parsing users from addOrUpdateTempUsers to groupmanagement failed!", e);
                throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorConstants.ERR_MSG_USER_500));
            }
            userService.updateTempUsers();
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorConstants.ERR_MSG_USER_500));
        }
    }

    public void sendInvitationEmailsToRound(final String containerUri) {
        final String requestUrl = createGroupManagementSendContainerEmailsUrl(containerUri);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        final HttpEntity<String> request = new HttpEntity<>(headers);
        final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.POST, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            LOG.debug("Invitation emails sent: " + response.getBody());
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorConstants.ERR_MSG_USER_500));
        }
    }

    private String createGroupmanagementBaseUrl() {
        return groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH + "/";
    }

    private String createGroupManagementTempUsersApiUrl(final String containerUri) {
        return createGroupmanagementBaseUrl() + GROUPMANAGEMENT_API_TEMPUSERS + "?containerUri=" + containerUri;
    }

    private String createGroupManagementSendContainerEmailsUrl(final String containerUri) {
        return createGroupmanagementBaseUrl() + GROUPMANAGEMENT_API_SENDCONTAINEREMAILS + "?containerUri=" + containerUri;
    }
}
