package fi.vm.yti.comments.api.resource.externalresources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.configuration.CodelistProperties;
import fi.vm.yti.security.AuthenticatedUserProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
@Path("/v1/codelist")
@Api(value = "codelist")
public class CodelistProxyResource implements AbstractIntegrationResource {

    private final CodelistProperties codelistProperties;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final RestTemplate restTemplate;

    @Inject
    public CodelistProxyResource(final CodelistProperties codelistProperties,
                                 final AuthenticatedUserProvider authenticatedUserProvider,
                                 final RestTemplate restTemplate) {
        this.codelistProperties = codelistProperties;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.restTemplate = restTemplate;
    }

    @GET
    @Path("/containers")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get containers from the Codelist API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getContainers(@ApiParam(value = "Language code for sorting results.") @QueryParam("language") final String language) {
        checkUser(authenticatedUserProvider);
        final String apiUrl = createCodelistContainerApiUrl() + "?language=" + language;
        return fetchIntegrationContainerData(apiUrl, restTemplate, HttpMethod.GET);
    }

    @POST
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get resources from the Codelist API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getResources(@ApiParam(value = "Request related parameters in request body.") @RequestBody final String searchQuery) {
        checkUser(authenticatedUserProvider);
        return fetchIntegrationResources(createCodelistResourcesApiUrl(), RESOURCES, restTemplate, HttpMethod.POST, searchQuery);
    }

    private String createCodelistContainerApiUrl() {
        return codelistProperties.getUrl() + "/" + CODELIST_API_CONTEXT_PATH + "/" + CODELIST_API_PATH + "/" + CODELIST_API_VERSION + "/" + API_INTEGRATION + "/" + API_CONTAINERS;
    }

    private String createCodelistResourcesApiUrl() {
        return codelistProperties.getUrl() + "/" + CODELIST_API_CONTEXT_PATH + "/" + CODELIST_API_PATH + "/" + CODELIST_API_VERSION + "/" + API_INTEGRATION + "/" + API_RESOURCES;
    }
}
