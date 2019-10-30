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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import static fi.vm.yti.comments.api.constants.ApiConstants.FILTER_NAME_ORGANIZATION;

@Component
@Path("/v1/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Organization")
public class OrganizationResource implements AbstractBaseResource {

    private final OrganizationService organizationService;

    @Inject
    public OrganizationResource(final OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Organizations API.")
    @ApiResponse(responseCode = "200", description = "Returns Organizations.", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationDTO.class))) })
    @Transactional
    public Response getOrganizations(@Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand,
                                     @Parameter(description = "A boolean value for only returning Organizations with CommentRounds.", in = ParameterIn.QUERY) @QueryParam("hasCommentRounds") final boolean hasCommentRounds) {
        ObjectWriterInjector.set(new AbstractBaseResource.FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_ORGANIZATION, expand)));
        final Set<OrganizationDTO> organizations = organizationService.findByRemovedIsFalse(hasCommentRounds);
        return createResponse("Organizations", MESSAGE_TYPE_GET_RESOURCES, organizations);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Operation(summary = "Organizations fetching API for single Organization.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns one single Organization.", content = { @Content(schema = @Schema(implementation = OrganizationDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "No Organization found with given UUID.")
    })
    @Transactional
    @Path("{organizationId}")
    public Response getOrganization(@Parameter(description = "Organization UUID.", in = ParameterIn.PATH, required = true) @PathParam("organizationId") final UUID organizationId,
                                    @Parameter(description = "Filter string (csl) for expanding specific child objects.", in = ParameterIn.QUERY) @QueryParam("expand") final String expand) {
        ObjectWriterInjector.set(new AbstractBaseResource.FilterModifier(createSimpleFilterProviderWithSingleFilter(FILTER_NAME_ORGANIZATION, expand)));
        final OrganizationDTO organization = organizationService.findById(organizationId);
        if (organization != null) {
            return Response.ok(organization).build();
        } else {
            throw new NotFoundException();
        }
    }
}
