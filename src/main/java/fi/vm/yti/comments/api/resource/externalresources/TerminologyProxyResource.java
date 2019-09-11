package fi.vm.yti.comments.api.resource.externalresources;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.configuration.TerminologyProperties;
import fi.vm.yti.comments.api.dto.IntegrationResourceRequestDTO;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.security.AuthenticatedUserProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
@Path("/v1/terminology")
@Api(value = "terminology")
public class TerminologyProxyResource implements AbstractIntegrationResource {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final TerminologyProperties terminologyProperties;
    private final RestTemplate restTemplate;

    @Inject
    public TerminologyProxyResource(final TerminologyProperties terminologyProperties,
                                    final AuthenticatedUserProvider authenticatedUserProvider,
                                    final RestTemplate restTemplate) {
        this.terminologyProperties = terminologyProperties;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.restTemplate = restTemplate;
    }

    @GET
    @Path("/containers")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get Containers from the Terminology API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getContainers() {
        checkUser(authenticatedUserProvider);
        return fetchIntegrationContainerData(createTerminologyContainerApiUrl(), restTemplate, HttpMethod.GET);
    }

    @POST
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get Resources from the Terminology API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getResources(@ApiParam(value = "Request related parameters in request body.") @RequestBody final String integrationRequestData) {
        checkUser(authenticatedUserProvider);
        final ObjectMapper mapper = new ObjectMapper();
        final IntegrationResourceRequestDTO request;
        try {
            request = mapper.readValue(integrationRequestData, new TypeReference<IntegrationResourceRequestDTO>() {
            });
        } catch (IOException e) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Malformed resources request body!"));
        }
        final String container = request.getContainer();
        if (container != null && !container.isEmpty()) {
            return fetchIntegrationResources(createTerminologyResourcesApiUrl(container), RESOURCES, restTemplate, HttpMethod.POST, null);
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_406));
        }
    }

    private String createTerminologyContainerApiUrl() {
        return terminologyProperties.getUrl() + "/" + API_INTEGRATION + "/" + API_CONTAINERS;
    }

    private String createTerminologyResourcesApiUrl(final String container) {
        return terminologyProperties.getUrl() + "/" + API_INTEGRATION + "/" + API_RESOURCES + "?container=" + container;
    }
}
