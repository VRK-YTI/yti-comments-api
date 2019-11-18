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

import fi.vm.yti.comments.api.configuration.TerminologyProperties;
import fi.vm.yti.comments.api.dto.IntegrationContainerRequestDTO;
import fi.vm.yti.comments.api.dto.IntegrationResourceRequestDTO;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.security.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;

@Component
@Produces({ "application/json", "application/xml" })
@Path("/v1/terminology")
@Tag(name = "Terminology")
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
    @Operation(summary = "Get Containers from the Terminology API.")
    @ApiResponse(responseCode = "200", description = "Returns success.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceDTO.class))) })
    public Response getContainers(@RequestBody(description = "Request related parameters in request body.", content = @Content(schema = @Schema(implementation = IntegrationContainerRequestDTO.class))) final String searchQuery) {
        checkUser(authenticatedUserProvider);
        return fetchIntegrationContainerData(createTerminologyContainerApiUrl(), restTemplate, HttpMethod.POST, searchQuery);
    }

    @POST
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Get Resources from the Terminology API.")
    @ApiResponse(responseCode = "200", description = "Returns success.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceDTO.class))) })
    public Response getResources(@RequestBody(description = "Request related parameters in request body.", content = @Content(schema = @Schema(implementation = IntegrationResourceRequestDTO.class))) final String searchQuery) {
        checkUser(authenticatedUserProvider);
        return fetchIntegrationResources(createTerminologyResourcesApiUrl(), RESOURCES, restTemplate, HttpMethod.POST, searchQuery);
    }

    private String createTerminologyContainerApiUrl() {
        return terminologyProperties.getUrl() + "/" + TERMINOLOGY_API_CONTEXT_PATH + "/" + TERMINOLOGY_API_PATH + "/" + TERMINOLOGY_API_VERSION + "/" + API_INTEGRATION + "/" + API_CONTAINERS;
    }

    private String createTerminologyResourcesApiUrl() {
        return terminologyProperties.getUrl() + "/" + TERMINOLOGY_API_CONTEXT_PATH + "/" + TERMINOLOGY_API_PATH + "/" + TERMINOLOGY_API_VERSION + "/" + API_INTEGRATION + "/" + API_RESOURCES;
    }
}
