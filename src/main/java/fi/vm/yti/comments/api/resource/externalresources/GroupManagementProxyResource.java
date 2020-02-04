package fi.vm.yti.comments.api.resource.externalresources;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
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
import fi.vm.yti.security.Role;
import fi.vm.yti.security.YtiUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_401;

@Component
@Path("/v1/groupmanagement")
@Tag(name = "GroupManagement")
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
    @Operation(summary = "Returns a list of user requests that the user has made.")
    @ApiResponse(responseCode = "200", description = "Returns success.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = GroupManagementUserRequest.class))) })
    public Response getUserRequests() {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        final String response = restTemplate.getForObject(createGroupManagementRequestsApiUrl(user.getId().toString()), String.class);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
        if (response != null) {
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
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User request API did not respond correctly!"));
        }
    }

    @POST
    @Path("/request")
    @Operation(summary = "Sends user request to add user to an organization to groupmanagement service.")
    @ApiResponse(responseCode = "200", description = "Returns success.")
    public Response sendUserRequest(@Parameter(description = "UUID for the requested organization.", required = true, in = ParameterIn.QUERY) @QueryParam("organizationId") final String organizationId) {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        final String requestUrl = createGroupManagementRequestApiUrl();
        final LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("userId", user.getId().toString());
        parameters.add("organizationId", organizationId);
        parameters.add("role", Role.MEMBER.toString());
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        final HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);
        final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Response.status(200).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String createGroupManagementRequestApiUrl() {
        return groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH + "/" + GROUPMANAGEMENT_API_REQUEST;
    }

    private String createGroupManagementRequestsApiUrl(final String userId) {
        return groupManagementProperties.getUrl() + "/" + GROUPMANAGEMENT_API_PRIVATE_CONTEXT_PATH + "/" + GROUPMANAGEMENT_API_REQUESTS + "?userId=" + userId;
    }
}
