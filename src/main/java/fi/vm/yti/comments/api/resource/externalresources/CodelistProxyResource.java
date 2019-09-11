package fi.vm.yti.comments.api.resource.externalresources;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.configuration.CodelistProperties;
import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import fi.vm.yti.security.AuthenticatedUserProvider;
import fi.vm.yti.security.YtiUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import static fi.vm.yti.comments.api.constants.ApiConstants.*;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_406;

@Component
@Path("/v1/codelist")
@Api(value = "codelist")
public class CodelistProxyResource implements AbstractIntegrationResource {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CodelistProperties codelistProperties;
    private final RestTemplate restTemplate;

    @Inject
    public CodelistProxyResource(final CodelistProperties codelistProperties,
                                 final AuthenticatedUserProvider authenticatedUserProvider,
                                 final RestTemplate restTemplate) {
        this.codelistProperties = codelistProperties;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.restTemplate = restTemplate;
    }

    @GET
    @Path("/containers")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get containers from the Codelist API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getContainers() {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException();
        }
        final String requestUrl = createCodelistContainerApiUrl();
        return fetchIntegrationContainerData(requestUrl, restTemplate, HttpMethod.GET);
    }

    @GET
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get resources from the Codelist API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getResources(@ApiParam(value = "Container URI for Resources.", required = true) @QueryParam("container") final String container,
                                 @ApiParam(value = "Language code for sorting results.") @QueryParam("language") @DefaultValue("fi") final String language,
                                 @ApiParam(value = "Pagination parameter for page size.") @QueryParam("pageSize") final Integer pageSize,
                                 @ApiParam(value = "Pagination parameter for start index.") @QueryParam("from") @DefaultValue("0") final Integer from,
                                 @ApiParam(value = "Status enumerations in CSL format.") @QueryParam("status") final String status,
                                 @ApiParam(value = "After date filtering parameter, results will be codes with modified date after this ISO 8601 formatted date string.") @QueryParam("after") final String after,
                                 @ApiParam(value = "Search term used to filter results based on partial prefLabel match.") @QueryParam("searchTerm") final String searchTerm) {
        return fetchResources(container, language, pageSize, from, status, after, searchTerm, null, HttpMethod.GET);
    }

    @POST
    @Path("/resources")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get resources from the Codelist API.")
    @ApiResponse(code = 200, message = "Returns success.")
    public Response getResources(@ApiParam(value = "Container URI for Resources.", required = true) @QueryParam("container") final String container,
                                 @ApiParam(value = "Language code for sorting results.") @QueryParam("language") @DefaultValue("fi") final String language,
                                 @ApiParam(value = "Pagination parameter for page size.") @QueryParam("pageSize") final Integer pageSize,
                                 @ApiParam(value = "Pagination parameter for start index.") @QueryParam("from") @DefaultValue("0") final Integer from,
                                 @ApiParam(value = "Status enumerations in CSL format.") @QueryParam("status") final String status,
                                 @ApiParam(value = "After date filtering parameter, results will be codes with modified date after this ISO 8601 formatted date string.") @QueryParam("after") final String after,
                                 @ApiParam(value = "Search term used to filter results based on partial prefLabel match.") @QueryParam("searchTerm") final String searchTerm,
                                 @ApiParam(value = "A set of resource URIs in CSL format to be excluded from the results.") @RequestBody final String excludedResourceUris) {
        return fetchResources(container, language, pageSize, from, status, after, searchTerm, excludedResourceUris, HttpMethod.POST);
    }

    private Response fetchResources(String container,
                                    String language,
                                    Integer pageSize,
                                    Integer from,
                                    String status,
                                    String after,
                                    String searchTerm,
                                    String excludedResourceUris,
                                    HttpMethod httpMethod) {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException();
        }
        if (container != null && !container.isEmpty()) {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("language", language);
            if (pageSize != null) {
                paramsMap.put("pageSize", pageSize.toString());
            }
            if (from != null) {
                paramsMap.put("from", from.toString());
            }
            if (status != null && !status.isEmpty()) {
                paramsMap.put("status", status);
            }
            if (after != null) {
                paramsMap.put("after", after);
            }
            if (searchTerm != null && !searchTerm.isEmpty()) {
                paramsMap.put("searchTerm", searchTerm);
            }

            return fetchIntegrationResources(createCodelistResourcesApiUrl(container, paramsMap), RESOURCES, restTemplate, httpMethod, excludedResourceUris);
        } else {
            throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_USER_406));
        }
    }

    private String createCodelistContainerApiUrl() {
        return codelistProperties.getUrl() + "/" + CODELIST_API_CONTEXT_PATH + "/" + CODELIST_API_PATH + "/" + CODELIST_API_VERSION + "/" + API_INTEGRATION + "/" + API_CONTAINERS;
    }

    private String createCodelistResourcesApiUrl(final String container,
                                                 Map<String, String> params) {
        String theUrl = codelistProperties.getUrl() + "/" + CODELIST_API_CONTEXT_PATH + "/" + CODELIST_API_PATH + "/" + CODELIST_API_VERSION + "/" + API_INTEGRATION + "/" + API_RESOURCES + "/?container=" + container;
        StringBuilder paramsBuilder = new StringBuilder("");
        params.forEach((key, value) -> { // yeah, not elegant, but not as bad as RestTemplate's official version of doing this.
            paramsBuilder.append("&" + key + "=" + value);
        });
        return theUrl + paramsBuilder.toString();

    }
}
