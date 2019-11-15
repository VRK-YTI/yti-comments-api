package fi.vm.yti.comments.api.resource;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.yti.comments.api.api.ResponseWrapper;
import fi.vm.yti.comments.api.dto.IntegrationResourceRequestDTO;
import fi.vm.yti.comments.api.dto.ResourceDTO;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.service.CommentRoundService;
import fi.vm.yti.comments.api.service.CommentThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component
@Path("/v1/integration")
@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
@Tag(name = "Integration")
public class IntegrationResource {

    private final CommentRoundService commentRoundService;
    private final CommentThreadService commentThreadService;

    @Inject
    public IntegrationResource(final CommentRoundService commentRoundService,
                               final CommentThreadService commentThreadService) {
        this.commentRoundService = commentRoundService;
        this.commentThreadService = commentThreadService;
    }

    @GET
    @Path("/containers")
    @Operation(description = "API for fetching container resources")
    @ApiResponse(responseCode = "200", description = "Returns container resources with meta element that shows details and a results list.")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getContainers(@Parameter(description = "Language code for sorting results.", in = ParameterIn.QUERY) @QueryParam("language") @DefaultValue("fi") final String language,
                                  @Parameter(description = "Pagination parameter for page size.", in = ParameterIn.QUERY) @QueryParam("pageSize") final Integer pageSize,
                                  @Parameter(description = "Pagination parameter for start index.", in = ParameterIn.QUERY) @QueryParam("from") @DefaultValue("0") final Integer from,
                                  @Parameter(description = "Status enumerations in CSL format.", in = ParameterIn.QUERY) @QueryParam("status") final String status,
                                  @Parameter(description = "After date filtering parameter, results will be codes with modified date after this ISO 8601 formatted date string.", in = ParameterIn.QUERY) @QueryParam("after") final String after,
                                  @Parameter(description = "Before date filtering parameter, results will be codes with modified date before this ISO 8601 formatted date string.", in = ParameterIn.QUERY) @QueryParam("before") final String before,
                                  @Parameter(description = "Search term used to filter results based on partial prefLabel or codeValue match.", in = ParameterIn.QUERY) @QueryParam("searchTerm") final String searchTerm,
                                  @Parameter(description = "Container URIs.", in = ParameterIn.QUERY) @Encoded @QueryParam("uri") final String uri,
                                  @Parameter(description = "User organizations filtering parameter, for filtering incomplete code lists", in = ParameterIn.QUERY) @QueryParam("includeIncompleteFrom") final String includeIncompleteFrom,
                                  @Parameter(description = "Control boolean for returning all incomplete containers.", in = ParameterIn.QUERY) @QueryParam("includeIncomplete") @DefaultValue("false") final Boolean includeIncomplete,
                                  @Parameter(description = "Pretty format JSON output.", in = ParameterIn.QUERY) @QueryParam("pretty") final String pretty) {
        final Meta meta = new Meta(200, pageSize, from, after, before);
        final Set<String> includedContainerUris;
        if (uri != null) {
            includedContainerUris = parseUris(uri);
        } else {
            includedContainerUris = null;
        }
        final Set<ResourceDTO> containers = commentRoundService.getContainers(includedContainerUris, meta);
        final ResponseWrapper<ResourceDTO> wrapper = new ResponseWrapper<>();
        wrapper.setResults(containers);
        wrapper.setMeta(meta);
        return Response.ok(wrapper).build();
    }

    @POST
    @Path("/containers")
    @Operation(description = "API for fetching container resources")
    @ApiResponse(responseCode = "200", description = "Returns container resources with meta element that shows details and a results list.")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getContainers(@Parameter(description = "Integration resource request parameters as JSON payload.") @RequestBody final String integrationRequestData) {
        final IntegrationResourceRequestDTO request = parseIntegrationRequestDto(integrationRequestData);
        final Integer pageSize = request.getPageSize();
        final Integer from = request.getPageFrom();
        final String after = request.getAfter();
        final String before = request.getBefore();
        final Set<String> includedContainerUuids = parseUrisFromList(request.getUri());
        final Meta meta = new Meta(200, pageSize, from, after, before);
        final Set<ResourceDTO> containers = commentRoundService.getContainers(includedContainerUuids, meta);
        final ResponseWrapper<ResourceDTO> wrapper = new ResponseWrapper<>();
        wrapper.setResults(containers);
        wrapper.setMeta(meta);
        return Response.ok(wrapper).build();
    }

    @GET
    @Path("/resources")
    @Operation(description = "API for fetching resources for a container")
    @ApiResponse(responseCode = "200", description = "Returns resources for a specific container with meta element that shows details and a results list.")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getResources(@Parameter(description = "Language code for sorting results.", in = ParameterIn.QUERY) @DefaultValue("fi") final String language,
                                 @Parameter(description = "Pagination parameter for page size.", in = ParameterIn.QUERY) @QueryParam("pageSize") final Integer pageSize,
                                 @Parameter(description = "Pagination parameter for start index.", in = ParameterIn.QUERY) @QueryParam("from") @DefaultValue("0") final Integer from,
                                 @Parameter(description = "Status enumerations in CSL format.", in = ParameterIn.QUERY) @QueryParam("status") final String status,
                                 @Parameter(description = "After date filtering parameter, results will be codes with modified date after this ISO 8601 formatted date string.", in = ParameterIn.QUERY) @QueryParam("after") final String after,
                                 @Parameter(description = "Before date filtering parameter, results will be codes with modified date before this ISO 8601 formatted date string.", in = ParameterIn.QUERY) @QueryParam("before") final String before,
                                 @Parameter(description = "Container URI.", in = ParameterIn.QUERY) @QueryParam("container") final String container,
                                 @Parameter(description = "Type for filtering resources.", in = ParameterIn.QUERY) @QueryParam("type") final String type,
                                 @Parameter(description = "Resource URIs.", in = ParameterIn.QUERY) @Encoded @QueryParam("uri") final String uri,
                                 @Parameter(description = "Search term used to filter results based on partial prefLabel or codeValue match.", in = ParameterIn.QUERY) @QueryParam("searchTerm") final String searchTerm,
                                 @Parameter(description = "User organizations filtering parameter, for filtering incomplete code lists", in = ParameterIn.QUERY) @QueryParam("includeIncompleteFrom") final String includeIncompleteFrom,
                                 @Parameter(description = "Control boolean for returning resources from incomplete code lists.", in = ParameterIn.QUERY) @QueryParam("includeIncomplete") @DefaultValue("false") final Boolean includeIncomplete,
                                 @Parameter(description = "Pretty format JSON output.", in = ParameterIn.QUERY) @QueryParam("pretty") final String pretty) {
        final Set<String> includedResourceUris;
        if (uri != null && !uri.isEmpty()) {
            includedResourceUris = parseUris(uri);
        } else {
            includedResourceUris = null;
        }
        final Meta meta = new Meta(200, pageSize, from, after, before);
        final Set<ResourceDTO> resources = commentThreadService.getResources(includedResourceUris, container, meta);
        final ResponseWrapper<ResourceDTO> wrapper = new ResponseWrapper<>();
        wrapper.setResults(resources);
        wrapper.setMeta(meta);
        return Response.ok(wrapper).build();
    }

    @POST
    @Path("/resources")
    @Operation(description = "API for fetching resources for a container")
    @ApiResponse(responseCode = "200", description = "Returns resources for a specific container with meta element that shows details and a results list.")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getResources(@Parameter(description = "Integration resource request parameters as JSON payload.") @RequestBody final String integrationRequestData) {
        final IntegrationResourceRequestDTO request = parseIntegrationRequestDto(integrationRequestData);
        final String container = request.getContainer();
        final Integer pageSize = request.getPageSize();
        final Integer from = request.getPageFrom();
        final String after = request.getAfter();
        final String before = request.getBefore();
        final Set<String> includedResourceUris = parseUrisFromList(request.getUri());
        final Meta meta = new Meta(200, pageSize, from, after, before);
        final Set<ResourceDTO> resources = commentThreadService.getResources(includedResourceUris, container, meta);
        final ResponseWrapper<ResourceDTO> wrapper = new ResponseWrapper<>();
        wrapper.setResults(resources);
        wrapper.setMeta(meta);
        return Response.ok(wrapper).build();
    }

    private IntegrationResourceRequestDTO parseIntegrationRequestDto(final String integrationRequestData) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(integrationRequestData, new TypeReference<IntegrationResourceRequestDTO>() {
            });
        } catch (IOException e) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), "Malformed resources in request body!"));
        }
    }

    private Set<String> parseUrisFromList(final List<String> uriSet) {
        if (uriSet != null && !uriSet.isEmpty()) {
            final Set<String> uuids = new HashSet<>();
            uuids.addAll(uriSet);
            return uuids;
        }
        return null;
    }

    private Set<String> parseUris(final String urisCsl) {
        if (urisCsl != null) {
            final Set<String> uriSet = new HashSet<>();
            for (final String uri : urisCsl.split(",")) {
                uriSet.add(uri.trim());
            }
            return uriSet;
        }
        return null;
    }
}
