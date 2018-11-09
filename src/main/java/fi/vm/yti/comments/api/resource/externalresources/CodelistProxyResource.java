package fi.vm.yti.comments.api.resource.externalresources;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import fi.vm.yti.comments.api.api.ResponseWrapper;
import fi.vm.yti.comments.api.configuration.CodelistProperties;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.resource.AbstractBaseResource;
import fi.vm.yti.security.AuthenticatedUserProvider;
import fi.vm.yti.security.YtiUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_401;

@Component
@Path("/v1/codelist")
@Api(value = "codelist")
public class CodelistProxyResource implements AbstractBaseResource {

    private static final Logger LOG = LoggerFactory.getLogger(CodelistProxyResource.class);
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CodelistProperties codelistProperties;
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
    @ApiOperation(value = "Get containers from codelist API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getContainers() {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        final String requestUrl = createCodelistContainerApiUrl();
        final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
                final Meta meta = new Meta();
                final ResponseWrapper<ResourceDTO> wrapper = new ResponseWrapper<>(meta);
                final Set<ResourceDTO> containers;
                final String data = response.getBody().toString();
                containers = mapper.readValue(data, new TypeReference<Set<ResourceDTO>>() {
                });
                meta.setCode(200);
                meta.setResultCount(containers.size());
                wrapper.setResults(containers);
                return Response.ok(wrapper).build();
            } catch (final IOException e) {
                LOG.error("Error parsing containers from codelist response! ", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            throw new NotFoundException();
        }
    }

    @GET
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get resources from codelist API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getResources(@ApiParam(value = "Container URI.", required = true) @QueryParam("container") final String container) {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        final String requestUrl;
        if (container != null && !container.isEmpty()) {
            requestUrl = createCodelistResourcesApiUrl() + "/?container=" + container;
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid request to codelist resources integration API."));
        }
        final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
                final Meta meta = new Meta();
                final ResponseWrapper<ResourceDTO> wrapper = new ResponseWrapper<>(meta);
                final Set<ResourceDTO> containers;
                final String data = response.getBody().toString();
                containers = mapper.readValue(data, new TypeReference<Set<ResourceDTO>>() {
                });
                meta.setCode(200);
                meta.setResultCount(containers.size());
                wrapper.setResults(containers);
                return Response.ok(wrapper).build();
            } catch (final IOException e) {
                LOG.error("Error parsing containers from codelist API response! ", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            throw new NotFoundException();
        }
    }

    private String createCodelistContainerApiUrl() {
        return codelistProperties.getUrl() + "/" + CODELIST_API_CONTEXT_PATH + "/" + CODELIST_API_PATH + "/" + CODELIST_API_VERSION + "/" + API_INTEGRATION + "/" + API_CONTAINERS;
    }

    private String createCodelistResourcesApiUrl() {
        return codelistProperties.getUrl() + "/" + CODELIST_API_CONTEXT_PATH + "/" + CODELIST_API_PATH + "/" + CODELIST_API_VERSION + "/" + API_INTEGRATION + "/" + API_RESOURCES;
    }
}
