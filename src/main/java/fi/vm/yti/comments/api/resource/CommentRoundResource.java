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
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.parser.CommentRoundParser;
import fi.vm.yti.comments.api.service.CommentRoundService;
import fi.vm.yti.comments.api.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENT;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENTROUND;

@Component
@Path("/v1/commentrounds")
@Api(value = "commentrounds")
public class CommentRoundResource implements AbstractBaseResource {

    private final CommentRoundService commentRoundService;
    private final CommentService commentService;
    private final CommentRoundParser commentRoundParser;

    public CommentRoundResource(final CommentRoundService commentRoundService,
                                final CommentService commentService,
                                final CommentRoundParser commentRoundParser) {
        this.commentRoundService = commentRoundService;
        this.commentService = commentService;
        this.commentRoundParser = commentRoundParser;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for requesting all commentRounds.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all commentRounds from the system as a list.")
    })
    @Transactional
    public Response getCommentRounds(@ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
        final Set<CommentRoundDTO> commentRoundDtos = commentRoundService.findAll();
        final Meta meta = new Meta();
        final ResponseWrapper<CommentRoundDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRounds found: " + commentRoundDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentRoundDtos);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for requesting single commentRound.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns single commentRound."),
        @ApiResponse(code = 404, message = "No commentRound found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}")
    public Response getCommentRound(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                    @ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
        final CommentRoundDTO commentRound = commentRoundService.findById(commentRoundId);
        if (commentRound != null) {
            return Response.ok(commentRound).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for requesting comments for commentRound.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns list of comments for this commentRound."),
        @ApiResponse(code = 404, message = "No commentRound found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/comments")
    public Response getCommentRoundComments(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                            @ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findByCommentRoundId(commentRoundId);
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRound comments found: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for creating or updating one or many commentRounds from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated commentRounds after storing them to database."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateCommentRounds(@ApiParam(value = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, null)));
        final Set<CommentRoundDTO> commentRoundDtos = commentRoundService.addOrUpdateCommentRoundsFromDtos(commentRoundParser.parseCommentRoundsFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentRoundDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRounds added or modified: " + commentRoundDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentRoundDtos);
        return Response.ok(responseWrapper).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for updating an existing commentRound.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updates a single existing commentRound."),
        @ApiResponse(code = 404, message = "No commentRound found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}")
    public Response updateCommentRound(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                       @ApiParam(value = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, null)));
        final CommentRoundDTO commentRound = commentRoundParser.parseCommentRoundFromJson(jsonPayload);
        if (commentRound != null) {
            return Response.ok(commentRound).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
