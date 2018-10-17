package fi.vm.yti.comments.api.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fi.vm.yti.comments.api.api.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@Component
@Path("/configuration")
@Api(value = "configuration")
@Produces("text/plain")
public class ConfigurationResource implements AbstractBaseResource {

    private final ApiUtils apiUtils;

    @Inject
    public ConfigurationResource(final ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @ApiOperation(value = "Get configuration values as JSON")
    @ApiResponse(code = 200, message = "Returns the configuration JSON element to the frontend related to this service.")
    public Response getConfig() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode configJson = mapper.createObjectNode();

        final String groupManagementPublicUrl = apiUtils.getGroupmanagementPublicUrl();
        final ObjectNode groupManagementConfig = mapper.createObjectNode();
        groupManagementConfig.put("url", groupManagementPublicUrl);
        configJson.set("groupManagementConfig", groupManagementConfig);

        final String datamodelPublicUrl = apiUtils.getDataModelPublicUrl();
        final ObjectNode dataModelConfig = mapper.createObjectNode();
        dataModelConfig.put("url", datamodelPublicUrl);
        configJson.set("dataModelConfig", dataModelConfig);

        final String terminologyPublicUrl = apiUtils.getTerminologyPublicUrl();
        final ObjectNode terminologyConfig = mapper.createObjectNode();
        terminologyConfig.put("url", terminologyPublicUrl);
        configJson.set("terminologyConfig", terminologyConfig);

        final String codelistPublicUrl = apiUtils.getCodelistPublicUrl();
        final ObjectNode codelistConfig = mapper.createObjectNode();
        codelistConfig.put("url", codelistPublicUrl);
        configJson.set("codelistConfig", codelistConfig);

        configJson.put("env", apiUtils.getEnv());
        return Response.ok(configJson).build();
    }
}