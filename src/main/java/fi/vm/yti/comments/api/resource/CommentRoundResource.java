package fi.vm.yti.comments.api.resource;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import fi.vm.yti.comments.api.dao.CommentDao;
import fi.vm.yti.comments.api.dao.CommentRoundDao;
import fi.vm.yti.comments.api.dao.CommentThreadDao;
import fi.vm.yti.comments.api.dto.CommentDTO;
import fi.vm.yti.comments.api.dto.CommentRoundDTO;
import fi.vm.yti.comments.api.dto.CommentThreadDTO;
import fi.vm.yti.comments.api.dto.OrganizationDTO;
import fi.vm.yti.comments.api.entity.Comment;
import fi.vm.yti.comments.api.entity.CommentRound;
import fi.vm.yti.comments.api.entity.CommentThread;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.export.ExportService;
import fi.vm.yti.comments.api.parser.CommentParser;
import fi.vm.yti.comments.api.parser.CommentRoundParser;
import fi.vm.yti.comments.api.parser.CommentThreadParser;
import fi.vm.yti.comments.api.security.AuthorizationManager;
import fi.vm.yti.comments.api.service.CommentRoundService;
import fi.vm.yti.comments.api.service.CommentService;
import fi.vm.yti.comments.api.service.CommentThreadService;
import fi.vm.yti.comments.api.utils.StringUtils;
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
    private final CommentThreadDao commentThreadDao;
    private final CommentDao commentDao;
    private final CommentThreadService commentThreadService;
    private final CommentService commentService;
    private final CommentRoundParser commentRoundParser;
    private final CommentThreadParser commentThreadParser;
    private final CommentParser commentParser;
    private final AuthorizationManager authorizationManager;
    private final ExportService exportService;

    @Inject
    public CommentRoundResource(final CommentRoundService commentRoundService,
                                final CommentRoundDao commentRoundDao,
                                final CommentThreadDao commentThreadDao,
                                final CommentDao commentDao,
                                final CommentThreadService commentThreadService,
                                final CommentService commentService,
                                final CommentRoundParser commentRoundParser,
                                final CommentThreadParser commentThreadParser,
                                final CommentParser commentParser,
                                final AuthorizationManager authorizationManager,
                                final ExportService exportService) {
        this.commentRoundService = commentRoundService;
        this.commentRoundDao = commentRoundDao;
        this.commentThreadDao = commentThreadDao;
        this.commentDao = commentDao;
        this.commentThreadService = commentThreadService;
        this.commentService = commentService;
        this.commentRoundParser = commentRoundParser;
        this.commentThreadParser = commentThreadParser;
        this.commentParser = commentParser;
        this.authorizationManager = authorizationManager;
        this.exportService = exportService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRound API for requesting all commentRounds.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all commentRounds from the system as a set.")
    })
    @Transactional
    public Response getCommentRounds(@ApiParam(value = "Filter option for organization filtering.") @QueryParam("organizationId") final UUID organizationId,
                                     @ApiParam(value = "Filter option for status filtering.") @QueryParam("status") final String status,
                                     @ApiParam(value = "Filter option for integration source type filtering.") @QueryParam("containerType") final String containerType,
                                     @ApiParam(value = "Filter option for incomplete filtering for round creator only") @QueryParam("filterIncomplete") @DefaultValue("false") final Boolean filterIncomplete,
                                     @ApiParam(value = "Filter string (csl) for expanding specific child recommentRounds.") @QueryParam("expand") final String expand,
                                     @ApiParam(value = "Filter by user organizations or user id.") @QueryParam("filterContent") final Boolean filterContent) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
        Set<CommentRoundDTO> commentRoundDtos;
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
        final UUID userUuid = authorizationManager.getUserId();
        if (filterIncomplete && commentRoundDtos != null) {
            commentRoundDtos = commentRoundDtos.stream().filter(commentRound -> {
                if (STATUS_INCOMPLETE.equalsIgnoreCase(commentRound.getStatus()) && userUuid != null && userUuid.equals(commentRound.getUser().getId()) || authorizationManager.isSuperUser()) {
                    return true;
                } else if (!STATUS_INCOMPLETE.equalsIgnoreCase(commentRound.getStatus())) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toSet());
        }
        final Set<CommentRoundDTO> commentRoundDtosToReturn = new HashSet<>();
        if (filterContent && commentRoundDtos != null && !commentRoundDtos.isEmpty()) {
            for (final CommentRoundDTO commentRoundDto : commentRoundDtos) {
                if (commentRoundDto.getUser().getId().equals(userUuid) || authorizationManager.isSuperUser()) {
                    commentRoundDtosToReturn.add(commentRoundDto);
                } else if (!"INCOMPLETE".equalsIgnoreCase(commentRoundDto.getStatus())) {
                    for (final OrganizationDTO organization : commentRoundDto.getOrganizations()) {
                        final Set<UUID> userOrganizationIds = authorizationManager.getUserOrganizations();
                        if (userOrganizationIds.contains(organization.getId())) {
                            commentRoundDtosToReturn.add(commentRoundDto);
                        }
                    }
                }
            }
        } else if (commentRoundDtos != null) {
            commentRoundDtosToReturn.addAll(commentRoundDtos);
        }
        final Meta meta = new Meta();
        final ResponseWrapper<CommentRoundDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRounds found: " + commentRoundDtosToReturn.size());
        meta.setCode(200);
        responseWrapper.setResults(commentRoundDtosToReturn);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8", "application/xlsx" })
    @ApiOperation(value = "CommentRound API for requesting single commentRound.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns single commentRound."),
        @ApiResponse(code = 404, message = "No commentRound found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}")
    public Response getCommentRound(@ApiParam(value = "CommentRound UUID.", required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                    @ApiParam(value = "Filter string (csl) for expanding specific child commentRounds.") @QueryParam("expand") final String expand,
                                    @ApiParam(value = "Format for output.") @QueryParam("format") @DefaultValue(FORMAT_JSON) final String format) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
        if (FORMAT_EXCEL.equalsIgnoreCase(format)) {
            final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
            if (commentRound != null) {
                return streamExcelOutput(exportService.exportCommentRoundToExcel(commentRound), "commentround.xlsx");
            } else {
                throw new NotFoundException();
            }
        } else {
            final CommentRoundDTO commentRound = commentRoundService.findById(commentRoundId);
            if (commentRound != null) {
                if (commentRound.getCommentThreads() != null) {
                    commentRound.setCommentThreads(commentRound.getCommentThreads().stream().sorted(Comparator.comparing(CommentThreadDTO::getResourceUri, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toCollection(LinkedHashSet::new)));
                }
                return Response.ok(commentRound).build();
            } else {
                throw new NotFoundException();
            }
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
        final Set<CommentDTO> sortedComments = commentDtos.stream().sorted(Comparator.comparing(CommentDTO::getCreated)).collect(Collectors.toCollection(LinkedHashSet::new));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRound main level comments found: " + sortedComments.size());
        meta.setCode(200);
        responseWrapper.setResults(sortedComments);
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
        final Set<CommentThreadDTO> sortedThreads = commentThreadDtos.stream().sorted(Comparator.comparing(CommentThreadDTO::getResourceUri, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toCollection(LinkedHashSet::new));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentThreadDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRound commentThreads found: " + sortedThreads.size());
        meta.setCode(200);
        responseWrapper.setResults(sortedThreads);
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
        if (!authorizationManager.canUserAddCommentRound()) {
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
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            if (!authorizationManager.canUserModifyCommentRound(commentRound)) {
                throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
            }
            ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, FILTER_NAME_COMMENTTHREAD)));
            final CommentRoundDTO commentRoundDto = commentRoundService.addOrUpdateCommentRoundFromDto(commentRoundParser.parseCommentRoundFromJson(jsonPayload));
            if (commentRoundDto != null) {
                return Response.ok(commentRoundDto).build();
            } else {
                throw new NotFoundException();
            }
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
        final Set<CommentThreadDTO> sortedThreads = commentThreadDtos.stream().sorted(Comparator.comparing(CommentThreadDTO::getResourceUri, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toCollection(LinkedHashSet::new));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentThreadDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentThreads added or modified: " + sortedThreads.size());
        meta.setCode(200);
        responseWrapper.setResults(sortedThreads);
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
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (!authorizationManager.canUserAddCommentsToCommentRound(commentRound)) {
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
        final Comment comment = commentDao.findById(commentId);
        if (!authorizationManager.canUserModifyComment(comment)) {
            throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, FILTER_NAME_COMMENTTHREAD + "," + FILTER_NAME_COMMENTROUND)));
        final CommentDTO commentDto = commentService.addOrUpdateCommentFromDto(commentRoundId, commentThreadId, commentParser.parseCommentFromJson(jsonPayload));
        if (commentDto != null) {
            return Response.ok(commentDto).build();
        } else {
            throw new NotFoundException();
        }
    }

    @DELETE
    @Path("{commentRoundId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Deletes a single existing CommentRound.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "CommentRound deleted."),
        @ApiResponse(code = 404, message = "CommentRound not found.")
    })
    public Response deleteCommentRound(@ApiParam(value = "CommentRound UUID", required = true) @PathParam("commentRoundId") final String commentRoundId) {
        final UUID commentRoundUuid = StringUtils.parseUUIDFromString(commentRoundId);
        final CommentRound existingCommentRound = commentRoundDao.findById(commentRoundUuid);
        if (existingCommentRound != null) {
            if (!authorizationManager.canUserDeleteCommentRound(existingCommentRound)) {
                throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
            }
            commentRoundService.deleteCommentRound(existingCommentRound);
        } else {
            return Response.status(404).build();
        }
        final Meta meta = new Meta();
        meta.setCode(200);
        final ResponseWrapper responseWrapper = new ResponseWrapper(meta);
        return Response.ok(responseWrapper).build();
    }

    @DELETE
    @Path("{commentRoundId}/commentThreads/{commentThreadId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Deletes a single existing CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "CommentThread deleted."),
        @ApiResponse(code = 404, message = "CommentThread not found.")
    })
    public Response deleteCommentRoundCommentThread(@ApiParam(value = "CommentRound UUID", required = true) @PathParam("commentRoundId") final String commentRoundId,
                                                    @ApiParam(value = "CommentThread UUID", required = true) @PathParam("commentThreadId") final String commentThreadId) {
        final UUID commentRoundUuid = StringUtils.parseUUIDFromString(commentRoundId);
        final CommentRound existingCommentRound = commentRoundDao.findById(commentRoundUuid);
        if (existingCommentRound != null) {
            final UUID commentThreadUuid = StringUtils.parseUUIDFromString(commentRoundId);
            final CommentThread existingCommentThread = commentThreadDao.findByCommentRoundAndId(existingCommentRound, commentThreadUuid);
            if (existingCommentThread != null) {
                if (!authorizationManager.canUserDeleteCommentThread(existingCommentRound)) {
                    throw new UnauthorizedException(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
                }
                commentThreadService.deleteCommentThread(existingCommentThread);
            } else {
                return Response.status(404).build();
            }
        } else {
            return Response.status(404).build();
        }
        final Meta meta = new Meta();
        meta.setCode(200);
        final ResponseWrapper responseWrapper = new ResponseWrapper(meta);
        return Response.ok(responseWrapper).build();
    }
}
