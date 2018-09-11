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
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.GlobalCommentsDTO;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.parser.GlobalCommentsParser;
import fi.vm.yti.comments.api.service.CommentService;
import fi.vm.yti.comments.api.service.GlobalCommentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENT;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_GLOBALCOMMENTS;

@Component
@Path("/v1/globalcomments")
@Api(value = "globalcomments")
public class GlobalCommentsResource implements AbstractBaseResource {

    private final GlobalCommentsService globalCommentsService;
    private final CommentService commentsService;
    private final GlobalCommentsParser globalCommentsParser;

    public GlobalCommentsResource(final GlobalCommentsService globalCommentsService,
                                  final CommentService commentsService,
                                  final GlobalCommentsParser globalCommentsParser) {
        this.globalCommentsService = globalCommentsService;
        this.commentsService = commentsService;
        this.globalCommentsParser = globalCommentsParser;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "GlobalComments API for requesting all globalComments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all globalComments from the system as a list.")
    })
    @Transactional
    public Response getAllGlobalComments(@ApiParam(value = "Filter string (csl) for expanding specific child types for globalComments.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_GLOBALCOMMENTS, expand)));
        final Set<GlobalCommentsDTO> globalCommentsDtos = globalCommentsService.findAll();
        final Meta meta = new Meta();
        final ResponseWrapper<GlobalCommentsDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("GlobalComments found: " + globalCommentsDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(globalCommentsDtos);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "GlobalComments API for requesting single globalComments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns single globalComments."),
        @ApiResponse(code = 404, message = "No globalComments found with given UUID.")
    })
    @Transactional
    @Path("{globalCommentsId}")
    public Response getGlobalComments(@ApiParam(value = "GlobalComments UUID.", required = true) @PathParam("globalCommentsId") final UUID globalCommentsId,
                                      @ApiParam(value = "Filter string (csl) for expanding specific child types for globalComments.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_GLOBALCOMMENTS, expand)));
        final GlobalCommentsDTO globalComments = globalCommentsService.findById(globalCommentsId);
        if (globalComments != null) {
            return Response.ok(globalComments).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "GlobalComments API for requesting comments for globalComments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns list of comments for this globalComments."),
        @ApiResponse(code = 404, message = "No globalComments found with given UUID.")
    })
    @Transactional
    @Path("{globalCommentsId}/comments")
    public Response getGlobalCommentsComments(@ApiParam(value = "GlobalComments UUID.", required = true) @PathParam("globalCommentsId") final UUID globalCommentsId,
                                              @ApiParam(value = "Filter string (csl) for expanding specific child types for comments.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentsService.findByGlobalCommentsId(globalCommentsId);
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("GlobalComments comments found: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "GlobalComments API for creating or updating one or many globalComments from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated globalComments after storing them to database."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateGlobalComments(@ApiParam(value = "JSON playload for globalComments data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(null, null)));
        final Set<GlobalCommentsDTO> globalCommentsDtos = globalCommentsService.addOrUpdateGlobalCommentsFromDtos(globalCommentsParser.parseGlobalCommentsSetFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<GlobalCommentsDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("GlobalComments added or modified: " + globalCommentsDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(globalCommentsDtos);
        return Response.ok(responseWrapper).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "GlobalComments API for updating an existing globalComments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updates a single existing globalComments."),
        @ApiResponse(code = 404, message = "No globalComments found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{globalCommentsId}")
    public Response updateGlobalComments(@ApiParam(value = "GlobalComments UUID.", required = true) @PathParam("globalCommentsId") final UUID globalCommentsId,
                                         @ApiParam(value = "JSON playload for globalComments data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(null, null)));
        final GlobalCommentsDTO globalComments = globalCommentsParser.parseGlobalCommentsFromJson(jsonPayload);
        if (globalComments != null) {
            return Response.ok(globalComments).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
