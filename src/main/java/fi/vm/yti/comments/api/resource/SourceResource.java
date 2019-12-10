package fi.vm.yti.comments.api.resource;

import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.parser.SourceParser;
import fi.vm.yti.comments.api.service.SourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_SOURCE;

@Component
@Path("/v1/sources")
@Tag(name = "Source")
public class SourceResource implements AbstractBaseResource {

    private final SourceService sourceService;
    private final SourceParser sourceParser;

    public SourceResource(final SourceService sourceService,
                          final SourceParser sourceParser) {
        this.sourceService = sourceService;
        this.sourceParser = sourceParser;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Source API for requesting all Sources.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns all Sources from the system as a list.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = SourceDTO.class))) })
    })
    @Transactional
    public Response getSources(@Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_SOURCE, expand)));
        final Set<SourceDTO> sourceDtos = sourceService.findAll();
        return createResponse("Sources", MESSAGE_TYPE_GET_RESOURCES, sourceDtos);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Source API for requesting single Source.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns single Source.", content = { @Content(schema = @Schema(implementation = SourceDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "No source found with given UUID.")
    })
    @Transactional
    @Path("{sourceId}")
    public Response getSource(@Parameter(description = "Source UUID.", in = ParameterIn.PATH, required = true) @PathParam("sourceId") final UUID sourceId,
                              @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_SOURCE, expand)));
        final SourceDTO source = sourceService.findById(sourceId);
        if (source != null) {
            return Response.ok(source).build();
        } else {
            throw new NotFoundException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Source API for creating or updating one or many Sources from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns created or updated Sources after storing them to database.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = SourceDTO.class))) }),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateSources(@Parameter(description = "JSON playload for source data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProvider(null, null)));
        final Set<SourceDTO> sourceDtos = sourceService.addOrUpdateSourcesFromDtos(sourceParser.parseSourcesFromJson(jsonPayload));
        return createResponse("Sources", MESSAGE_TYPE_ADDED_OR_MODIFIED, sourceDtos);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Source API for updating an existing Source.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updates a single existing Source.", content = { @Content(schema = @Schema(implementation = SourceDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "No Source found with given UUID."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{sourceId}")
    public Response updateSource(@Parameter(description = "Source UUID.", in = ParameterIn.PATH, required = true) @PathParam("sourceId") final UUID sourceId,
                                 @Parameter(description = "JSON playload for source data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_SOURCE, null)));
        final SourceDTO source = sourceParser.parseSourceFromJson(jsonPayload);
        if (source != null) {
            return Response.ok(source).build();
        } else {
            throw new NotFoundException();
        }
    }
}
