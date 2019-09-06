package fi.vm.yti.comments.api.resource.externalresources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.configuration.DatamodelProperties;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.security.AuthenticatedUserProvider;
import fi.vm.yti.security.YtiUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
@Path("/v1/datamodel")
@Api(value = "datamodel")
public class DatamodelProxyResource implements AbstractIntegrationResource {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final DatamodelProperties datamodelProperties;
    private final RestTemplate restTemplate;

    @Inject
    public DatamodelProxyResource(final DatamodelProperties datamodelProperties,
                                  final AuthenticatedUserProvider authenticatedUserProvider,
                                  final RestTemplate restTemplate) {
        this.datamodelProperties = datamodelProperties;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.restTemplate = restTemplate;
    }

    @GET
    @Path("/containers")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get Containers from the Datamodel API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getContainers() {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException();
        }
        return fetchIntegrationContainerData(createDatamodelContainerApiUrl(), restTemplate, HttpMethod.GET);
    }

    @GET
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get Resources from the Datamodel API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getResources(@ApiParam(value = "Container URI for Resources.", required = true) @QueryParam("container") final String container) {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException();
        }
        if (container != null && !container.isEmpty()) {
            return fetchIntegrationResourceData(createDatamodelResourcesApiUrl(container), restTemplate, HttpMethod.GET);
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_406));
        }
    }

    private String createDatamodelContainerApiUrl() {
        return datamodelProperties.getUrl() + "/" + API_BASE_PATH + "/" + API_REST + "/" + API_INTEGRATION + "/" + API_CONTAINERS;
    }

    private String createDatamodelResourcesApiUrl(final String container) {
        return datamodelProperties.getUrl() + "/" + API_BASE_PATH + "/" + API_REST + "/" + API_INTEGRATION + "/" + API_RESOURCES + "/?container=" + container;
    }
}
