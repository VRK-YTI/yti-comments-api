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
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.service.CommentService;
import fi.vm.yti.comments.api.service.CommentThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENT;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENTTHREAD;

@Component
@Path("/v1/threads")
public class CommentThreadResource implements AbstractBaseResource {

    private final CommentService commentService;
    private final CommentThreadService commentThreadService;

    public CommentThreadResource(final CommentService commentService,
                                 final CommentThreadService commentThreadService) {
        this.commentService = commentService;
        this.commentThreadService = commentThreadService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentThread API for requesting all CommentThreads.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns all CommentThreads.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentThreadDTO.class))) })
    })
    @Tag(name = "CommentThread")
    @Transactional
    public Response getCommentThreads(@Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, expand)));
        final Set<CommentThreadDTO> commentThreadDtos = commentThreadService.findAll();
        return createResponse("CommentThreads", MESSAGE_TYPE_GET_RESOURCES, commentThreadDtos);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentThread API for requesting single existing CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns one CommentThread matching UUID.", content = { @Content(schema = @Schema(implementation = CommentThreadDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "No CommentThread found with given UUID.")
    })
    @Tag(name = "CommentThread")
    @Transactional
    @Path("{commentThreadId}")
    public Response getCommentThread(@Parameter(description = "CommentThread UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                     @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, expand)));
        final CommentThreadDTO commentThread = commentThreadService.findById(commentThreadId);
        if (commentThread != null) {
            return Response.ok(commentThread).build();
        } else {
            throw new NotFoundException();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentThread API for requesting Comments for CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns list of Comments for this CommentThread.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentRoundDTO.class))) }),
        @ApiResponse(responseCode = "404", description = "No CommentThread found with given UUID.")
    })
    @Tag(name = "Comment")
    @Transactional
    @Path("{commentThreadId}/comments")
    public Response getCommentThreadComments(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                             @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final CommentThreadDTO commentThread = commentThreadService.findById(commentThreadId);
        if (commentThread != null) {
            final Set<CommentDTO> commentDtos = commentService.findByCommentThreadId(commentThreadId);
            return createResponse("CommentThread Comments", MESSAGE_TYPE_GET_RESOURCES, commentDtos);
        } else {
            throw new NotFoundException();
        }
    }
}
