package fi.vm.yti.comments.api.resource;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.api.ResponseWrapper;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.parser.CommentParser;
import fi.vm.yti.comments.api.parser.CommentRoundParser;
import fi.vm.yti.comments.api.parser.CommentThreadParser;
import fi.vm.yti.comments.api.security.AuthorizationManager;
import fi.vm.yti.comments.api.service.CommentRoundService;
import fi.vm.yti.comments.api.service.CommentService;
import fi.vm.yti.comments.api.service.CommentThreadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_401;

@Component
@Path("/v1/commentrounds")
@Api(value = "commentrounds")
public class CommentRoundResource implements AbstractBaseResource {

    private final CommentRoundService commentRoundService;
    private final CommentRoundDao commentRoundDao;
    private final CommentThreadService commentThreadService;
    private final CommentService commentService;
    private final CommentRoundParser commentRoundParser;
    private final CommentThreadParser commentThreadParser;
    private final CommentParser commentParser;
    private final AuthorizationManager authorizationManager;

    @Inject
    public CommentRoundResource(final CommentRoundService commentRoundService,
                                final CommentRoundDao commentRoundDao,
                                final CommentThreadService commentThreadService,
                                final CommentService commentService,
                                final CommentRoundParser commentRoundParser,
                                final CommentThreadParser commentThreadParser,
                                final CommentParser commentParser,
                                final AuthorizationManager authorizationManager) {
        this.commentRoundService = commentRoundService;
        this.commentRoundDao = commentRoundDao;
        this.commentThreadService = commentThreadService;
        this.commentService = commentService;
        this.commentRoundParser = commentRoundParser;
        this.commentThreadParser = commentThreadParser;
        this.commentParser = commentParser;
        this.authorizationManager = authorizationManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for requesting all commentRounds.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all commentRounds from the system as a list.")
    })
    @Transactional
    public Response getCommentRounds(@ApiParam(value = "Filter option for organization filtering.") @QueryParam("organizationId") final UUID organizationId,
                                     @ApiParam(value = "Filter option for status filtering.") @QueryParam("status") final String status,
                                     @ApiParam(value = "Filter option for integration source type filtering.") @QueryParam("containerType") final String containerType,
                                     @ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
        final Set<CommentRoundDTO> commentRoundDtos;
        if (organizationId != null && status != null && containerType != null) {
            commentRoundDtos = commentRoundService.findByOrganizationsIdAndStatusAndSourceContainerType(organizationId, status, containerType);
        } else if (organizationId != null && status != null) {
            commentRoundDtos = commentRoundService.findByOrganizationsIdAndStatus(organizationId, status);
        } else if (organizationId != null && containerType != null) {
            commentRoundDtos = commentRoundService.findByOrganizationsIdAndSourceContainerType(organizationId, containerType);
        } else if (status != null && containerType != null) {
            commentRoundDtos = commentRoundService.findByStatusAndSourceContainerType(status, containerType);
        } else if (organizationId != null) {
            commentRoundDtos = commentRoundService.findByOrganizationsId(organizationId);
        } else if (status != null) {
            commentRoundDtos = commentRoundService.findByStatus(status);
        } else if (containerType != null) {
            commentRoundDtos = commentRoundService.findBySourceContainerType(containerType);
        } else {
            commentRoundDtos = commentRoundService.findAll();
        }
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
                                    @ApiParam(value = "Filter string (csl) for expanding specific child commentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
        final CommentRoundDTO commentRound = commentRoundService.findById(commentRoundId);
        if (commentRound != null) {
            return Response.ok(commentRound).build();
        } else {
            throw new NotFoundException();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for requesting users entry comments for each thread.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns list of comments for this commentThread."),
        @ApiResponse(code = 404, message = "No commmentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/mycomments")
    public Response getCommentRoundMyMainComments(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                  @ApiParam(value = "Filter string (csl) for expanding specific child commentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findCommentRoundMainLevelCommentsForUserId(commentRoundId, authorizationManager.getUserId());
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRound main level comments found: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for requesting comments for commentThread.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns list of comments for this commentThread."),
        @ApiResponse(code = 404, message = "No commentRound found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/")
    public Response getCommentRoundCommentThreads(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                  @ApiParam(value = "Filter string (csl) for expanding specific child commentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, expand)));
        final Set<CommentThreadDTO> commentThreadDtos = commentThreadService.findByCommentRoundId(commentRoundId);
        final Meta meta = new Meta();
        final ResponseWrapper<CommentThreadDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRound commentThreads found: " + commentThreadDtos.size());
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
    @Path("{commentRoundId}/commentthreads/{commentThreadId}")
    public Response getCommentRoundCommentThread(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                 @ApiParam(value = "CommentThread UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
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
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments")
    public Response getCommentRoundCommentThreadComments(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                         @ApiParam(value = "CommentThread UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
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

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentThread API for requesting comments for commentThread.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns a single comment for this commentThread."),
        @ApiResponse(code = 404, message = "No commmentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments/{commentId}")
    public Response getCommentRoundCommentThreadComments(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                         @ApiParam(value = "CommentThread UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                         @ApiParam(value = "Comment UUID.", required = true) @PathParam("commentId") final UUID commentId,
                                                         @ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final CommentDTO commentDto = commentService.findById(commentId);
        if (commentDto != null) {
            return Response.ok(commentDto).build();
        } else {
            throw new NotFoundException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for creating or updating one or many commentRounds from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated commentRounds after storing them to database."),
        @ApiResponse(code = 401, message = "Not authorized for given action."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateCommentRounds(@ApiParam(value = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        if (!authorizationManager.isSuperUser()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, FILTER_NAME_COMMENTTHREAD)));
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
        @ApiResponse(code = 401, message = "Not authorized for given action."),
        @ApiResponse(code = 404, message = "No commentRound found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}")
    public Response updateCommentRound(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                       @ApiParam(value = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        if (!authorizationManager.isSuperUser()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, FILTER_NAME_COMMENTTHREAD)));
        final CommentRoundDTO commentRound = commentRoundService.addOrUpdateCommentRoundFromDto(commentRoundParser.parseCommentRoundFromJson(jsonPayload));
        if (commentRound != null) {
            return Response.ok(commentRound).build();
        } else {
            throw new NotFoundException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for creating or updating one or many comments from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated commentts after storing them to database."),
        @ApiResponse(code = 401, message = "Not authorized for given action."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/comments")
    public Response createOrUpdateCommentRoundComments(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                       @ApiParam(value = "JSON playload for commentRound commentThread data.", required = true) final String jsonPayload) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (!authorizationManager.canUserAddCommentsToCommentRound(commentRound)) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, FILTER_NAME_COMMENTTHREAD + "," + FILTER_NAME_COMMENTROUND)));
        final Set<CommentDTO> commentDtos = commentService.addOrUpdateCommentsFromDtos(commentRoundId, commentParser.parseCommentsFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("Comments added or modified: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for creating or updating one or many commentThreads from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated commentThreads after storing them to database."),
        @ApiResponse(code = 401, message = "Not authorized for given action."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads")
    public Response createOrUpdateCommentRoundCommentThreads(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                             @ApiParam(value = "JSON playload for commentRound commentThread data.", required = true) final String jsonPayload) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (!authorizationManager.canUserAddCommentThreadsToCommentRound(commentRound)) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, FILTER_NAME_COMMENT)));
        final Set<CommentThreadDTO> commentThreadDtos = commentThreadService.addOrUpdateCommentThreadsFromDtos(commentRoundId, commentThreadParser.parseCommentThreadsFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentThreadDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentThreads added or modified: " + commentThreadDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentThreadDtos);
        return Response.ok(responseWrapper).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for updating an existing commentThread.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updates a single existing commentThread."),
        @ApiResponse(code = 401, message = "Not authorized for given action."),
        @ApiResponse(code = 404, message = "No commentRound found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}")
    public Response updateCommentRoundCommentThread(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                    @ApiParam(value = "CommentThread UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                    @ApiParam(value = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        if (!authorizationManager.isSuperUser()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, FILTER_NAME_COMMENT)));
        final CommentThreadDTO commentThread = commentThreadService.addOrUpdateCommentThreadFromDto(commentRoundId, commentThreadParser.parseCommentThreadFromJson(jsonPayload));
        if (commentThread != null) {
            return Response.ok(commentThread).build();
        } else {
            throw new NotFoundException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for creating or updating one or many comments from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated commentts after storing them to database."),
        @ApiResponse(code = 401, message = "Not authorized for given action."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments")
    public Response createOrUpdateCommentRoundCommentThreadsComments(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                                     @ApiParam(value = "CommentThread UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                                     @ApiParam(value = "JSON playload for commentRound commentThread data.", required = true) final String jsonPayload) {
        if (!authorizationManager.isSuperUser()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, FILTER_NAME_COMMENTTHREAD + "," + FILTER_NAME_COMMENTROUND)));
        final Set<CommentDTO> commentDtos = commentService.addOrUpdateCommentsFromDtos(commentRoundId, commentThreadId, commentParser.parseCommentsFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("Comments added or modified: " + commentDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentDtos);
        return Response.ok(responseWrapper).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for updating an existing comment.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updates a single existing comment."),
        @ApiResponse(code = 401, message = "Not authorized for given action."),
        @ApiResponse(code = 404, message = "No comment found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments/{commentId}")
    public Response updateCommentRoundCommentThreadComment(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                           @ApiParam(value = "CommentThread UUID.", required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                           @ApiParam(value = "Comment UUID.", required = true) @PathParam("commentId") final UUID commentId,
                                                           @ApiParam(value = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        if (!authorizationManager.isSuperUser()) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, FILTER_NAME_COMMENTTHREAD + "," + FILTER_NAME_COMMENTROUND)));
        final CommentDTO comment = commentService.addOrUpdateCommentFromDto(commentRoundId, commentThreadId, commentParser.parseCommentFromJson(jsonPayload));
        if (comment != null) {
            return Response.ok(comment).build();
        } else {
            throw new NotFoundException();
        }
    }
}
