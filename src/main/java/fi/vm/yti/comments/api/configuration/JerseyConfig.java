package fi.vm.yti.comments.api.configuration;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.exception.exceptionmapping.UncaughtExceptionMapper;
import fi.vm.yti.comments.api.exception.exceptionmapping.YtiCommentsExceptionMapper;
import fi.vm.yti.comments.api.filter.CacheFilter;
import fi.vm.yti.comments.api.filter.RequestLoggingFilter;
import fi.vm.yti.comments.api.resource.AuthenticatedUserResource;
import fi.vm.yti.comments.api.resource.CommentResource;
import fi.vm.yti.comments.api.resource.CommentRoundResource;
import fi.vm.yti.comments.api.resource.CommentThreadResource;
import fi.vm.yti.comments.api.resource.ConfigurationResource;
import fi.vm.yti.comments.api.resource.ImpersonateUserResource;
import fi.vm.yti.comments.api.resource.OrganizationResource;
import fi.vm.yti.comments.api.resource.PingResource;
import fi.vm.yti.comments.api.resource.SourceResource;
import fi.vm.yti.comments.api.resource.SwaggerResource;
import fi.vm.yti.comments.api.resource.externalresources.CodelistProxyResource;
import fi.vm.yti.comments.api.resource.externalresources.DatamodelProxyResource;
import fi.vm.yti.comments.api.resource.externalresources.GroupManagementProxyResource;
import fi.vm.yti.comments.api.resource.externalresources.TerminologyProxyResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import static fi.vm.yti.comments.api.constants.ApiConstants.API_BASE_PATH;
import static fi.vm.yti.comments.api.constants.ApiConstants.API_CONTEXT_PATH;

@Component
@SwaggerDefinition(
    info = @Info(
        description = "YTI Comments - Comments API - Spring Boot microservice.",
        version = "v1",
        title = "YTI Comments - Comments API Service",
        termsOfService = "https://opensource.org/licenses/EUPL-1.1",
        contact = @Contact(
            name = "Content commenting Service by the Population Register Center of Finland",
            url = "https://yhteentoimiva.suomi.fi/",
            email = "yhteentoimivuus@vrk.fi"
        ),
        license = @License(
            name = "EUPL-1.2",
            url = "https://opensource.org/licenses/EUPL-1.1"
        )
    ),
    host = "localhost:9701",
    basePath = "/" + API_CONTEXT_PATH + "/" + API_BASE_PATH,
    consumes = { MediaType.APPLICATION_JSON },
    produces = { MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN },
    schemes = { SwaggerDefinition.Scheme.HTTPS }
)
@Api(value = "/api")
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        final JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(new CustomObjectMapper());

        // Cache control headers to no cache.
        register(CacheFilter.class);

        // ExceptionMappers
        register(YtiCommentsExceptionMapper.class);
        register(UncaughtExceptionMapper.class);

        // Logging
        register(RequestLoggingFilter.class);

        // Ping test API
        register(PingResource.class);

        // Swagger
        register(SwaggerResource.class);

        // GroupManagement
        register(GroupManagementProxyResource.class);

        // Authentication
        register(AuthenticatedUserResource.class);
        register(ImpersonateUserResource.class);

        // Configuration
        register(ConfigurationResource.class);

        // Integrations
        register(CodelistProxyResource.class);
        register(DatamodelProxyResource.class);
        register(TerminologyProxyResource.class);

        // APIs
        register(SourceResource.class);
        register(CommentResource.class);
        register(CommentRoundResource.class);
        register(CommentThreadResource.class);
        register(OrganizationResource.class);
    }
}
