package fi.vm.yti.comments.api.resource.externalresources;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.configuration.CodelistProperties;
import fi.vm.yti.comments.api.dto.IntegrationContainerRequestDTO;
import fi.vm.yti.comments.api.dto.IntegrationResourceRequestDTO;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.security.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
@Produces({ "application/json", "application/xml" })
@Path("/v1/codelist")
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

    @POST
    @Path("/containers")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Get containers from the Codelist API.")
    @ApiResponse(responseCode = "200", description = "Returns success.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceDTO.class))) })
    public Response getContainers(@Parameter(description = "Request related parameters in request body.") @RequestBody(content = @Content(schema = @Schema(implementation = IntegrationContainerRequestDTO.class))) final String searchQuery) {
        checkUser(authenticatedUserProvider);
        return fetchIntegrationContainerData(createCodelistContainerApiUrl(), restTemplate, HttpMethod.POST, searchQuery);
    }

    @POST
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Get resources from the Codelist API.")
    @ApiResponse(responseCode = "200", description = "Returns success.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceDTO.class))) })
    public Response getResources(@Parameter(description = "Request related parameters in request body.") @RequestBody(content = @Content(schema = @Schema(implementation = IntegrationResourceRequestDTO.class))) final String searchQuery) {
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
