package fi.vm.yti.comments.api.resource;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.exception.NotFoundException;
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

    public CommentResource(final CommentService commentService) {
        this.commentService = commentService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Comment API for requesting all comments.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all comments from the system as a list.")
    })
    @Transactional
    public Response getComments(@ApiParam(value = "Filter string (csl) for expanding specific child objects.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findAll();
        return createResponse("Comments", MESSAGE_TYPE_GET_RESOURCES, commentDtos);
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
                               @ApiParam(value = "Filter string (csl) for expanding specific child objects.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final CommentDTO comment = commentService.findById(commentId);
        if (comment != null) {
            return Response.ok(comment).build();
        } else {
            throw new NotFoundException();
        }
    }
}
