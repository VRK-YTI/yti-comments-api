package fi.vm.yti.comments.api.resource.externalresources;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.configuration.TerminologyProperties;
import fi.vm.yti.security.AuthenticatedUserProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
@Path("/v1/terminology")
@Api(value = "terminology")
public class TerminologyProxyResource implements AbstractIntegrationResource {

    private final TerminologyProperties terminologyProperties;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final RestTemplate restTemplate;

    @Inject
    public TerminologyProxyResource(final TerminologyProperties terminologyProperties,
                                    final AuthenticatedUserProvider authenticatedUserProvider,
                                    final RestTemplate restTemplate) {
        this.terminologyProperties = terminologyProperties;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.restTemplate = restTemplate;
    }

    @POST
    @Path("/containers")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get Containers from the Terminology API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getContainers(@ApiParam(value = "Request related parameters in request body.") @RequestBody final String searchQuery) {
        checkUser(authenticatedUserProvider);
        return fetchIntegrationContainerData(createTerminologyContainerApiUrl(), restTemplate, HttpMethod.POST, searchQuery);
    }

    @POST
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get Resources from the Terminology API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getResources(@ApiParam(value = "Request related parameters in request body.") @RequestBody final String searchQuery) {
        checkUser(authenticatedUserProvider);
        return fetchIntegrationResources(createTerminologyResourcesApiUrl(), RESOURCES, restTemplate, HttpMethod.POST, searchQuery);
    }

    private String createTerminologyContainerApiUrl() {
        return terminologyProperties.getUrl() + "/" + API_INTEGRATION + "/" + API_CONTAINERS;
    }

    private String createTerminologyResourcesApiUrl() {
        return terminologyProperties.getUrl() + "/" + API_INTEGRATION + "/" + API_RESOURCES;
    }
}
