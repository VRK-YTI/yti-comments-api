package fi.vm.yti.comments.api.resource;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;
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

import fi.vm.yti.comments.api.api.ResponseWrapper;
import fi.vm.yti.comments.api.dto.SourceDTO;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.parser.SourceParser;
import fi.vm.yti.comments.api.service.SourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_SOURCE;

@Component
@Path("/v1/sources")
@Api(value = "sources")
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
    @ApiOperation(value = "Source API for requesting all sources.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all sources from the system as a list.")
    })
    @Transactional
    public Response getSources(@ApiParam(value = "Filter string (csl) for expanding specific child resources.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_SOURCE, expand)));
        final Set<SourceDTO> sourceDtos = sourceService.findAll();
        final Meta meta = new Meta();
        final ResponseWrapper<SourceDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("Sources found: " + sourceDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(sourceDtos);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Source API for requesting single source.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns single source."),
        @ApiResponse(code = 404, message = "No source found with given UUID.")
    })
    @Transactional
    @Path("{sourceId}")
    public Response getSource(@ApiParam(value = "Source UUID.", required = true) @PathParam("sourceId") final UUID sourceId,
                              @ApiParam(value = "Filter string (csl) for expanding specific child resources.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_SOURCE, expand)));
        final SourceDTO source = sourceService.findById(sourceId);
        if (source != null) {
            return Response.ok(source).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Source API for creating or updating one or many sources from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated sources after storing them to database."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateSources(@ApiParam(value = "JSON playload for source data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProvider(null, null)));
        final Set<SourceDTO> sourceDtos = sourceService.addOrUpdateSourcesFromDtos(sourceParser.parseSourcesFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<SourceDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("Sources added or modified: " + sourceDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(sourceDtos);
        return Response.ok(responseWrapper).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Source API for updating an existing source.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updates a single existing source."),
        @ApiResponse(code = 404, message = "No source found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{sourceId}")
    public Response updateSource(@ApiParam(value = "Source UUID.", required = true) @PathParam("sourceId") final UUID sourceId,
                                 @ApiParam(value = "JSON playload for source data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_SOURCE, null)));
        final SourceDTO source = sourceParser.parseSourceFromJson(jsonPayload);
        if (source != null) {
            return Response.ok(source).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
