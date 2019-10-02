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
import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.comments.api.export.ExportService;
import fi.vm.yti.comments.api.parser.CommentParser;
import fi.vm.yti.comments.api.parser.CommentRoundParser;
import fi.vm.yti.comments.api.parser.CommentThreadParser;
import fi.vm.yti.comments.api.security.AuthorizationManager;
import fi.vm.yti.comments.api.service.CommentRoundService;
import fi.vm.yti.comments.api.service.CommentService;
import fi.vm.yti.comments.api.service.CommentThreadService;
import fi.vm.yti.comments.api.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_OTHER_USER_ALREADY_RESPONDED_TO_THIS_COMMENT_CANT_MODIFY_OR_DELETE;

@Component
@Path("/v1/commentrounds")
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
    @Operation(summary = "CommentRound API for requesting all commentRounds.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns all commentRounds from the system as a set.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentRoundDTO.class))) })
    })
    @Transactional
    public Response getCommentRounds(@Parameter(description = "Filter option for organization filtering.", in = ParameterIn.QUERY) @QueryParam("organizationId") final UUID organizationId,
                                     @Parameter(description = "Filter option for status filtering.", in = ParameterIn.QUERY) @QueryParam("status") final String status,
                                     @Parameter(description = "Filter option for integration source type filtering.", in = ParameterIn.QUERY) @QueryParam("containerType") final String containerType,
                                     @Parameter(description = "Filter option for integration source name match.", in = ParameterIn.QUERY) @QueryParam("searchTerm") final String searchTerm,
                                     @Parameter(description = "Filter option for incomplete filtering for round creator only", in = ParameterIn.QUERY) @QueryParam("filterIncomplete") @DefaultValue("false") final Boolean filterIncomplete,
                                     @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                     @Parameter(description = "Filter by user organizations or user id.", in = ParameterIn.QUERY) @QueryParam("filterContent") @DefaultValue("false") final boolean filterContent) {
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
                } else {
                    return !STATUS_INCOMPLETE.equalsIgnoreCase(commentRound.getStatus());
                }
            }).collect(Collectors.toSet());
        }

        if (searchTerm != null && !searchTerm.isEmpty() && commentRoundDtos != null) {
            commentRoundDtos = commentRoundDtos.stream().filter(commentRound -> {
                if (commentRound.getLabel().toUpperCase().startsWith(searchTerm.toUpperCase()) || commentRound.getLabel().toUpperCase().endsWith(searchTerm.toUpperCase())) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toSet());
        }

        final Set<CommentRoundDTO> commentRoundDtosToReturn = new HashSet<>();
        if (filterContent && commentRoundDtos != null && !commentRoundDtos.isEmpty()) {
            for (final CommentRoundDTO commentRoundDto : commentRoundDtos) {
                if ((commentRoundDto.getUser() != null && commentRoundDto.getUser().getId().equals(userUuid)) || authorizationManager.isSuperUser()) {
                    commentRoundDtosToReturn.add(commentRoundDto);
                } else if (!STATUS_INCOMPLETE.equalsIgnoreCase(commentRoundDto.getStatus())) {
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
        return createResponse("CommentRounds", MESSAGE_TYPE_GET_RESOURCES, commentRoundDtosToReturn);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8", "application/xlsx" })
    @Operation(summary = "CommentRound API for requesting single commentRound.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns single commentRound.", content= { @Content(schema = @Schema(implementation = CommentRoundDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "No CommentRound found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}")
    public Response getCommentRound(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                    @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                    @Parameter(description = "Format for output.", in = ParameterIn.QUERY) @QueryParam("format") @DefaultValue(FORMAT_JSON) final String format) {
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
    @Operation(summary = "CommentRound API for requesting users entry comments for each thread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns list of comments for this commentThread.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))) }),
        @ApiResponse(responseCode = "404", description = "No CommentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/mycomments")
    public Response getCommentRoundMyMainComments(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                  @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findCommentRoundMainLevelCommentsForUserId(commentRoundId, authorizationManager.getUserId());
        final Set<CommentDTO> sortedComments = commentDtos.stream().sorted(Comparator.comparing(CommentDTO::getCreated)).collect(Collectors.toCollection(LinkedHashSet::new));
        return createResponse("CommentRound top level Comments", MESSAGE_TYPE_GET_RESOURCES, sortedComments);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentRound API for requesting comments for commentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns list of comments for this commentThread.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))) }),
        @ApiResponse(responseCode = "404", description = "No commentRound found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/")
    public Response getCommentRoundCommentThreads(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                  @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, expand)));
        final Set<CommentThreadDTO> commentThreadDtos = commentThreadService.findByCommentRoundId(commentRoundId);
        final Set<CommentThreadDTO> sortedThreads = commentThreadDtos.stream().sorted(Comparator.comparing(CommentThreadDTO::getResourceUri, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toCollection(LinkedHashSet::new));
        return createResponse("CommentRound CommentThreads", MESSAGE_TYPE_GET_RESOURCES, sortedThreads);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentThread API for requesting single existing CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns one CommentThread matching UUID.", content = { @Content(schema = @Schema(implementation = CommentThreadDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "No CommentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}")
    public Response getCommentRoundCommentThread(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                 @Parameter(description = "CommentThread UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
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
    @Operation(summary = "CommentThread API for requesting comments for CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns list of comments for this CommentThread.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))) }),
        @ApiResponse(responseCode = "404", description = "No CommentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments")
    public Response getCommentRoundCommentThreadComments(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                         @Parameter(description = "CommentThread UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                         @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
        final Set<CommentDTO> commentDtos = commentService.findByCommentThreadId(commentThreadId);
        return createResponse("CommentThread Comments", MESSAGE_TYPE_GET_RESOURCES, commentDtos);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentThread API for requesting a single Commment for CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns a single Comment for this CommentThread.", content = { @Content(schema = @Schema(implementation = CommentDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "No CommentThread found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments/{commentId}")
    public Response getCommentRoundCommentThreadComments(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                         @Parameter(description = "CommentThread UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                         @Parameter(description = "Comment UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentId") final UUID commentId,
                                                         @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
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
    @Operation(summary = "CommentRound API for creating or updating one or many CommentRounds from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns created or updated CommentRounds after storing them to database.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentRoundDTO.class))) }),
        @ApiResponse(responseCode = "401", description = "Not authorized for given action."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateCommentRounds(@Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                                @Parameter(description = "Remove orphan CommentThread objects", in = ParameterIn.QUERY) @QueryParam("removeCommentThreadOrphans") @DefaultValue("false") final boolean removeCommentThreadOrphans,
                                                @RequestBody(description = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        if (authorizationManager.canUserAddCommentRound()) {
            ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
            final Set<CommentRoundDTO> commentRoundDtos = commentRoundService.addOrUpdateCommentRoundsFromDtos(commentRoundParser.parseCommentRoundsFromJson(jsonPayload), removeCommentThreadOrphans);
            return createResponse("CommentRounds", MESSAGE_TYPE_ADDED_OR_MODIFIED, commentRoundDtos);
        } else {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentRound API for updating an existing CommentRound.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updates a single existing CommentRound.", content = { @Content(schema = @Schema(implementation = CommentDTO.class)) }),
        @ApiResponse(responseCode = "401", description = "Not authorized for given action."),
        @ApiResponse(responseCode = "404", description = "No CommentRound found with given UUID."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}")
    public Response updateCommentRound(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                       @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                       @Parameter(description = "Remove orphan CommentThread objects", in = ParameterIn.QUERY) @QueryParam("removeCommentThreadOrphans") @DefaultValue("false") final boolean removeCommentThreadOrphans,
                                       @RequestBody(description = "JSON playload for CommentRound data.", required = true) final String jsonPayload) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (commentRound != null) {
            if (authorizationManager.canUserModifyCommentRound(commentRound)) {
                ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUND, expand)));
                final CommentRoundDTO commentRoundDto = commentRoundService.addOrUpdateCommentRoundFromDto(commentRoundParser.parseCommentRoundFromJson(jsonPayload), removeCommentThreadOrphans);
                if (commentRoundDto != null) {
                    return Response.ok(commentRoundDto).build();
                } else {
                    throw new NotFoundException();
                }
            } else {
                throw new UnauthorizedException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentRound API for creating or updating one or many Comments from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns created or updated Comments after storing them to database.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))) }),
        @ApiResponse(responseCode = "401", description = "Not authorized for given action."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/comments")
    public Response createOrUpdateCommentRoundComments(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                       @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                                       @RequestBody(description = "JSON playload for commentRound commentThread data.", required = true) final String jsonPayload) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (authorizationManager.canUserAddCommentsToCommentRound(commentRound)) {
            ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
            final Set<CommentDTO> commentDtos = commentService.addOrUpdateCommentsFromDtos(commentRoundId, commentParser.parseCommentsFromJson(jsonPayload));
            return createResponse("Comments", MESSAGE_TYPE_ADDED_OR_MODIFIED, commentDtos);
        } else {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentRound API for creating or updating one or many CommentThreads from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns created or updated CommentThreads after storing them to database.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentThreadDTO.class))) }),
        @ApiResponse(responseCode = "401", description = "Not authorized for given action."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads")
    public Response createOrUpdateCommentRoundCommentThreads(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                             @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                                             @Parameter(description = "Remove orphans", in = ParameterIn.QUERY) @QueryParam("removeOrphans") @DefaultValue("false") final boolean removeOrphans,
                                                             @RequestBody(description = "JSON playload for commentRound commentThread data.", required = true) final String jsonPayload) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (authorizationManager.canUserAddCommentThreadsToCommentRound(commentRound)) {
            ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, expand)));
            final Set<CommentThreadDTO> commentThreadDtos = commentThreadService.addOrUpdateCommentThreadsFromDtos(commentRoundId, commentThreadParser.parseCommentThreadsFromJson(jsonPayload), removeOrphans);
            final Set<CommentThreadDTO> sortedThreads = commentThreadDtos.stream().sorted(Comparator.comparing(CommentThreadDTO::getResourceUri, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toCollection(LinkedHashSet::new));
            return createResponse("CommentThreads", MESSAGE_TYPE_ADDED_OR_MODIFIED, sortedThreads);
        } else {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentRound API for updating an existing CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updates a single existing CommentThread.", content = { @Content(schema = @Schema(implementation = CommentThreadDTO.class)) }),
        @ApiResponse(responseCode = "401", description = "Not authorized for given action."),
        @ApiResponse(responseCode = "404", description = "No CommentRound found with given UUID."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}")
    public Response updateCommentRoundCommentThread(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                    @Parameter(description = "CommentThread UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                    @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                                    @RequestBody(description = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        if (authorizationManager.isSuperUser()) {
            ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTTHREAD, expand)));
            final CommentThreadDTO commentThread = commentThreadService.addOrUpdateCommentThreadFromDto(commentRoundId, commentThreadParser.parseCommentThreadFromJson(jsonPayload));
            if (commentThread != null) {
                return Response.ok(commentThread).build();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentRound API for creating or updating one or many Comments from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns created or updated Comments after storing them to database.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDTO.class))) }),
        @ApiResponse(responseCode = "401", description = "Not authorized for given action."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments")
    public Response createOrUpdateCommentRoundCommentThreadComments(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                                    @Parameter(description = "CommentThread UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                                    @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                                                    @RequestBody(description = "JSON playload for commentRound commentThread data.", required = true) final String jsonPayload) {
        final CommentRound commentRound = commentRoundDao.findById(commentRoundId);
        if (authorizationManager.canUserAddCommentsToCommentRound(commentRound)) {
            ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
            final Set<CommentDTO> commentDtos = commentService.addOrUpdateCommentsFromDtos(commentRoundId, commentThreadId, commentParser.parseCommentsFromJson(jsonPayload));
            return createResponse("Comments", MESSAGE_TYPE_ADDED_OR_MODIFIED, commentDtos);
        } else {
            throw new UnauthorizedException();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "CommentRound API for updating an existing Comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Updates a single existing Comment.", content = { @Content(schema = @Schema(implementation = CommentDTO.class)) }),
        @ApiResponse(responseCode = "401", description = "Not authorized for given action."),
        @ApiResponse(responseCode = "404", description = "No Comment found with given UUID."),
        @ApiResponse(responseCode = "406", description = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments/{commentId}")
    public Response updateCommentRoundCommentThreadComment(@Parameter(description = "CommentRound UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final UUID commentRoundId,
                                                           @Parameter(description = "CommentThread UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final UUID commentThreadId,
                                                           @Parameter(description = "Comment UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentId") final UUID commentId,
                                                           @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                                           @RequestBody(description = "JSON playload for commentRound data.", required = true) final String jsonPayload) {
        final Comment comment = commentDao.findById(commentId);
        boolean problemWithAuth = !authorizationManager.canUserDeleteComment(comment);
        boolean problemWithConcurrentModification = commentService.commentHasChildren(comment);
        if (problemWithAuth) {
            throw new UnauthorizedException();
        }
        if (problemWithConcurrentModification) {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_OTHER_USER_ALREADY_RESPONDED_TO_THIS_COMMENT_CANT_MODIFY_OR_DELETE));
        }
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENT, expand)));
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
    @Operation(summary = "Deletes a single existing CommentRound.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CommentRound deleted.", content = { @Content(schema = @Schema(implementation = ResponseWrapper.class)) }),
        @ApiResponse(responseCode = "404", description = "CommentRound not found.")
    })
    public Response deleteCommentRound(@Parameter(description = "CommentRound UUID", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final String commentRoundId) {
        final UUID commentRoundUuid = StringUtils.parseUUIDFromString(commentRoundId);
        final CommentRound existingCommentRound = commentRoundDao.findById(commentRoundUuid);
        if (existingCommentRound != null) {
            if (authorizationManager.canUserDeleteCommentRound(existingCommentRound)) {
                commentRoundService.deleteCommentRound(existingCommentRound);
            } else {
                throw new UnauthorizedException();
            }
        } else {
            throw new NotFoundException();
        }
        return createDeleteResponse("CommentRound");
    }

    @DELETE
    @Path("{commentRoundId}/commentThreads/{commentThreadId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Deletes a single existing CommentThread.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CommentThread deleted.", content = { @Content(schema = @Schema(implementation = ResponseWrapper.class)) }),
        @ApiResponse(responseCode = "404", description = "CommentThread not found.")
    })
    public Response deleteCommentRoundCommentThread(@Parameter(description = "CommentRound UUID", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final String commentRoundId,
                                                    @Parameter(description = "CommentThread UUID", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final String commentThreadId) {
        final UUID commentRoundUuid = StringUtils.parseUUIDFromString(commentRoundId);
        final CommentRound existingCommentRound = commentRoundDao.findById(commentRoundUuid);
        if (existingCommentRound != null) {
            final UUID commentThreadUuid = StringUtils.parseUUIDFromString(commentRoundId);
            final CommentThread existingCommentThread = commentThreadDao.findByCommentRoundAndId(existingCommentRound, commentThreadUuid);
            if (existingCommentThread != null) {
                if (authorizationManager.canUserDeleteCommentThread(existingCommentRound)) {
                    commentThreadService.deleteCommentThread(existingCommentThread);
                } else {
                    throw new UnauthorizedException();
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
        return createDeleteResponse("CommentThread");
    }

    @DELETE
    @Path("{commentRoundId}/commentthreads/{commentThreadId}/comments/{commentId}/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Deletes a single existing Comment.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment deleted.", content = { @Content(schema = @Schema(implementation = ResponseWrapper.class)) }),
        @ApiResponse(responseCode = "404", description = "Comment not found.")
    })
    public Response deleteCommentRoundCommentThreadComment(@Parameter(description = "CommentRound UUID", in = ParameterIn.PATH, required = true) @PathParam("commentRoundId") final String commentRoundId,
                                                           @Parameter(description = "CommentThread UUID", in = ParameterIn.PATH, required = true) @PathParam("commentThreadId") final String commentThreadId,
                                                           @Parameter(description = "Comment UUID.", in = ParameterIn.PATH, required = true) @PathParam("commentId") final UUID commentId) {
        final Comment existingComment = commentDao.findById(commentId);
        if (existingComment != null) {
            boolean problemWithAuth = !authorizationManager.canUserDeleteComment(existingComment);
            boolean problemWithConcurrentModification = commentService.commentHasChildren(existingComment);
            if (problemWithAuth) {
                throw new UnauthorizedException();
            }
            if (problemWithConcurrentModification) {
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_OTHER_USER_ALREADY_RESPONDED_TO_THIS_COMMENT_CANT_MODIFY_OR_DELETE));
            }
            commentService.deleteComment(existingComment);
        } else {
            throw new NotFoundException();
        }
        return createDeleteResponse("Comment");
    }
}
