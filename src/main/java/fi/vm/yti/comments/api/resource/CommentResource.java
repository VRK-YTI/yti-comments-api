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
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.parser.CommentParser;
import fi.vm.yti.comments.api.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENT;

@Component
@Path("/v1/comments")
@Api(value = "comments")
public class CommentResource implements AbstractBaseResource {

    private final CommentService commentService;
    private final CommentParser commentParser;

    public CommentResource(final CommentService commentService,
                           final CommentParser commentParser) {
        this.commentService = commentService;
        this.commentParser = commentParser;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Comment API for requesting all comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all comments from the system as a list.")
    })
    @Transactional
    public Response getComments(@ApiParam(value = "Filter string (csl) for expanding specific child recomments.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findAll();
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("Comments found: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Comment API for requesting single comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns single comment."),
        @ApiResponse(code = 404, message = "No comment found with given UUID.")
    })
    @Transactional
    @Path("{commentId}")
    public Response getComment(@ApiParam(value = "Comment UUID.", required = true) @PathParam("commentId") final UUID commentId,
                               @ApiParam(value = "Filter string (csl) for expanding specific child recomments.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final CommentDTO comment = commentService.findById(commentId);
        if (comment != null) {
            return Response.ok(comment).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Comment API for creating or updating one or many comments from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated comments after storing them to database."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateComments(@ApiParam(value = "JSON playload for comment data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, null)));
        final Set<CommentDTO> commentDtos = commentService.addOrUpdateCommentsFromDtos(commentParser.parseCommentsFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("Comments added or modified: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Comment API for updating an existing comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updates a single existing comment."),
        @ApiResponse(code = 404, message = "No comment found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentId}")
    public Response updateComment(@ApiParam(value = "Comment UUID.", required = true) @PathParam("commentId") final UUID commentId,
                                  @ApiParam(value = "JSON playload for comment data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, null)));
        final CommentDTO comment = commentParser.parseCommentFromJson(jsonPayload);
        if (comment != null) {
            return Response.ok(comment).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
