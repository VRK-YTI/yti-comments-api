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

import fi.vm.yti.comments.api.api.ResponseWrapper;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.service.CommentService;
import fi.vm.yti.comments.api.service.CommentThreadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENT;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENTTHREAD;

@Component
@Path("/v1/threads")
@Api(value = "threads")
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
    @ApiOperation(value = "CommentThread API for requesting all commentThreads.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns list of comments for this commentThread."),
        @ApiResponse(code = 404, message = "No commmentThread found with given UUID.")
    })
    @Transactional
    public Response getCommentThreads(@ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, expand)));
        final Set<CommentThreadDTO> commentThreadDtos = commentThreadService.findAll();
        final Meta meta = new Meta();
        final ResponseWrapper<CommentThreadDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentThread comments found: " + commentThreadDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentThreadDtos);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentThread API for requesting single existing commentThread.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns one commentThread matching UUID."),
        @ApiResponse(code = 404, message = "No commmentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentThreadId}")
    public Response getCommentThread(@ApiParam(value = "CommentThread UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                     @ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
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
    @ApiOperation(value = "CommentThread API for requesting comments for commentThread.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns list of comments for this commentThread."),
        @ApiResponse(code = 404, message = "No commmentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentThreadId}/comments")
    public Response getCommentThreadComments(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                             @ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findByCommentThreadId(commentThreadId);
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentThread comments found: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();
    }
}
