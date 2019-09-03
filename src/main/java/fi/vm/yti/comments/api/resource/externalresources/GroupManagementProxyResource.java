package fi.vm.yti.comments.api.resource.externalresources;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import fi.vm.yti.comments.api.api.ResponseWrapper;
import fi.vm.yti.comments.api.configuration.GroupManagementProperties;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.groupmanagement.GroupManagementUserRequest;
import fi.vm.yti.comments.api.resource.AbstractBaseResource;
import fi.vm.yti.security.AuthenticatedUserProvider;
import fi.vm.yti.security.YtiUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.GROUPMANAGEMENT_API_CONTEXT_PATH;
import static fi.vm.yti.comments.api.constants.ApiConstants.GROUPMANAGEMENT_API_REQUESTS;

@Component
@Path("/v1/groupmanagement")
@Api(value = "groupmanagement")
public class GroupManagementProxyResource implements AbstractBaseResource {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final GroupManagementProperties groupManagementProperties;
    private final RestTemplate restTemplate;

    @Inject
    public GroupManagementProxyResource(final GroupManagementProperties groupManagementProperties,
                                        final AuthenticatedUserProvider authenticatedUserProvider,
                                        final RestTemplate restTemplate) {
        this.groupManagementProperties = groupManagementProperties;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.restTemplate = restTemplate;
    }

    @GET
    @Path("/requests")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Returns a list of user requests that the user has made.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getUserRequests() {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException();
        }
        final String response = restTemplate.getForObject(createGroupManagementRequestsApiUrl(user.getEmail()), String.class);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        try {
            final Set<GroupManagementUserRequest> userRequests = mapper.readValue(response, new TypeReference<Set<GroupManagementUserRequest>>() {
            });
            final Meta meta = new Meta();
            final ResponseWrapper<GroupManagementUserRequest> wrapper = new ResponseWrapper<>(meta);
            meta.setCode(200);
            meta.setResultCount(userRequests.size());
            wrapper.setResults(userRequests);
            return Response.ok(wrapper).build();
        } catch (final IOException e) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error parsing userRequests from groupmanagement!"));
        }
    }

    private String createGroupManagementRequestsApiUrl(final String email) {
        return groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_CONTEXT_PATH + "/" + GROUPMANAGEMENT_API_REQUESTS + "?email=" + email;
    }
}
