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
import fi.vm.yti.comments.api.dto.CommentRoundGroupDTO;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.parser.CommentRoundGroupParser;
import fi.vm.yti.comments.api.service.CommentRoundGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_COMMENTROUNDGROUP;

@Component
@Path("/v1/commentroundgroups")
@Api(value = "commentroundgroups")
public class CommentRoundGroupResource implements AbstractBaseResource {

    private final CommentRoundGroupService commentRoundGroupService;
    private final CommentRoundGroupParser commentRoundGroupParser;

    public CommentRoundGroupResource(final CommentRoundGroupService commentRoundGroupService,
                                     final CommentRoundGroupParser commentRoundGroupParser) {
        this.commentRoundGroupService = commentRoundGroupService;
        this.commentRoundGroupParser = commentRoundGroupParser;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRoundGroup API for requesting all commentRoundGroups.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns all commentRoundGroups from the system as a list.")
    })
    @Transactional
    public Response getCommentRoundGroups(@ApiParam(value = "Filter string (csl) for expanding specific child recommentRoundGroups.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUNDGROUP, expand)));
        final Set<CommentRoundGroupDTO> commentRoundGroupDtos = commentRoundGroupService.findAll();
        final Meta meta = new Meta();
        final ResponseWrapper<CommentRoundGroupDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRoundGroups found: " + commentRoundGroupDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentRoundGroupDtos);
        return Response.ok(responseWrapper).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRoundGroup API for requesting single commentRoundGroup.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns single commentRoundGroup."),
        @ApiResponse(code = 404, message = "No commentRoundGroup found with given UUID.")
    })
    @Transactional
    @Path("{commentRoundGroupId}")
    public Response getCommentRoundGroup(@ApiParam(value = "CommentRoundGroup UUID.", required = true) @PathParam("commentRoundGroupId") final UUID commentRoundGroupId,
                                         @ApiParam(value = "Filter string (csl) for expanding specific child recommentRoundGroups.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUNDGROUP, expand)));
        final CommentRoundGroupDTO commentRoundGroup = commentRoundGroupService.findById(commentRoundGroupId);
        if (commentRoundGroup != null) {
            return Response.ok(commentRoundGroup).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRoundGroup API for creating or updating one or many commentRoundGroups from a list type JSON payload.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Returns created or updated commentRoundGroups after storing them to database."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    public Response createOrUpdateCommentRoundGroups(@ApiParam(value = "JSON playload for commentRoundGroup data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUNDGROUP, null)));
        final Set<CommentRoundGroupDTO> commentRoundGroupDtos = commentRoundGroupService.addOrUpdateCommentRoundGroupsFromDtos(commentRoundGroupParser.parseCommentRoundGroupsFromJson(jsonPayload));
        final Meta meta = new Meta();
        final ResponseWrapper<CommentRoundGroupDTO> responseWrapper = new ResponseWrapper<>(meta);
        meta.setMessage("CommentRoundGroups added or modified: " + commentRoundGroupDtos.size());
        meta.setCode(200);
        responseWrapper.setResults(commentRoundGroupDtos);
        return Response.ok(responseWrapper).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "CommentRoundGroup API for updating an existing commentRoundGroup.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Updates a single existing commentRoundGroup."),
        @ApiResponse(code = 404, message = "No commentRoundGroup found with given UUID."),
        @ApiResponse(code = 406, message = "Data payload error, please check input data.")
    })
    @Transactional
    @Path("{commentRoundGroupId}")
    public Response updateCommentRoundGroup(@ApiParam(value = "CommentRoundGroup UUID.", required = true) @PathParam("commentRoundGroupId") final UUID commentRoundGroupId,
                                            @ApiParam(value = "JSON playload for commentRoundGroup data.", required = true) final String jsonPayload) {
        ObjectWriterInjector.set(new FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_COMMENTROUNDGROUP, null)));
        final CommentRoundGroupDTO commentRoundGroup = commentRoundGroupParser.parseCommentRoundGroupFromJson(jsonPayload);
        if (commentRoundGroup != null) {
            return Response.ok(commentRoundGroup).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
