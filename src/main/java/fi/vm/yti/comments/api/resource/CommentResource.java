package fi.vm.yti.comments.api.resource;

import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENT;

@Component
@Path("/v1/comments")
@Tag(name = "Comment")
public class CommentResource implements AbstractBaseResource {

    private final CommentService commentService;

    public CommentResource(final CommentService commentService) {
        this.commentService = commentService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Comment API for requesting all comments.")
    @ApiResponse(responseCode = "200", description = "Returns all comments from the system as a list.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))) })
    @Transactional
    public Response getComments(@Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findAll();
        return createResponse("Comments", MESSAGE_TYPE_GET_RESOURCES, commentDtos);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Comment API for requesting single comment.")
    @ApiResponse(responseCode = "200", description = "Returns single comment.", content = { @Content(schema = @Schema(implementation = CommentDTO.class)) })
    @ApiResponse(responseCode = "404", description = "No comment found with given UUID.")
    @Transactional
    @Path("{commentId}")
    public Response getComment(@Parameter(description = "Comment UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentId") final UUID commentId,
                               @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final CommentDTO comment = commentService.findById(commentId);
        if (comment != null) {
            return Response.ok(comment).build();
        } else {
            throw new NotFoundException();
        }
    }
}
