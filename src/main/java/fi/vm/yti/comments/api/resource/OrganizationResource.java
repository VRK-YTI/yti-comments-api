package fi.vm.yti.comments.api.resource;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.dto.OrganizationDTO;
import fi.vm.yti.comments.api.service.OrganizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_ORGANIZATION;

@Component
@Path("/v1/organizations")
@Api(value = "organizations")
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationResource implements AbstractBaseResource {

    private final OrganizationService organizationService;

    @Inject
    public OrganizationResource(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Organizations API.")
    @ApiResponse(code = 200, message = "Returns Organizations.")
    @Transactional
    public Response getOrganizations(@ApiParam(value = "Filter string (csl) for expanding specific child objects.") @QueryParam("expand") final String expand,
                                     @ApiParam(value = "A boolean value for only returning Organizations with CommentRounds.") @QueryParam("hasCommentRounds") final boolean hasCommentRounds) {
        ObjectWriterInjector.set(new AbstractBaseResource.FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_ORGANIZATION, expand)));
        final Set<OrganizationDTO> organizations = organizationService.findByRemovedIsFalse(hasCommentRounds);
        return createResponse("Organizations", MESSAGE_TYPE_GET_RESOURCES, organizations);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Organizations fetching API for single Organization.")
    @ApiResponse(code = 200, message = "Returns one single Organization.")
    @Transactional
    @Path("{organizationId}")
    public Response getOrganization(@ApiParam(value = "Organization UUID.", required = true) @PathParam("organizationId") final UUID organizationId,
                                    @ApiParam(value = "Filter string (csl) for expanding specific child objects.") @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new AbstractBaseResource.FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_ORGANIZATION, expand)));
        final OrganizationDTO organization = organizationService.findById(organizationId);
        if (organization != null) {
            return Response.ok(organization).build();
        } else {
            throw new NotFoundException();
        }
    }
}
