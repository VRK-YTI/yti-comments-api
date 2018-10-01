package fi.vm.yti.comments.api.groupmanagement;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.configuration.GroupManagementProperties;
import fi.vm.yti.comments.api.configuration.ImpersonateProperties;
import static fi.vm.yti.comments.api.constants.ApiConstants.GROUPMANAGEMENT_API_CONTEXT_PATH;
import static fi.vm.yti.comments.api.constants.ApiConstants.GROUPMANAGEMENT_API_USERS;
import static org.springframework.http.HttpMethod.GET;

@Component
public class ImpersonateUserService {

    private final GroupManagementProperties groupManagementProperties;
    private final ImpersonateProperties fakeLoginProperties;
    private final RestTemplate restTemplate;

    @Inject
    public ImpersonateUserService(final GroupManagementProperties groupManagementProperties,
                                  final ImpersonateProperties fakeLoginProperties,
                                  final RestTemplate restTemplate) {
        this.groupManagementProperties = groupManagementProperties;
        this.fakeLoginProperties = fakeLoginProperties;
        this.restTemplate = restTemplate;
    }

    @NotNull
    public List<GroupManagementUser> getUsers() {
        if (fakeLoginProperties.isAllowed()) {
            String url = groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_CONTEXT_PATH + "/" + GROUPMANAGEMENT_API_USERS;
            return restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<GroupManagementUser>>() {
            }).getBody();
        } else {
            return Collections.emptyList();
        }
    }
}
