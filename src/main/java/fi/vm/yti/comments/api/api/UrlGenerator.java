package fi.vm.yti.comments.api.api;

import org.springframework.stereotype.Component;

import fi.vm.yti.comments.api.configuration.CommentsApiConfiguration;
import static fi.vm.yti.comments.api.constants.ApiConstants.API_BASE_PATH;
import static fi.vm.yti.comments.api.constants.ApiConstants.API_VERSION;

@Component
public class UrlGenerator {

    private final CommentsApiConfiguration commentsApiProperties;

    public UrlGenerator(final CommentsApiConfiguration commentsApiProperties) {
        this.commentsApiProperties = commentsApiProperties;
    }

    public String createResourceUrl(final String apiPath,
                                    final String resourceId) {
        final String port = commentsApiProperties.getPort();
        final StringBuilder builder = new StringBuilder();
        builder.append(commentsApiProperties.getScheme());
        builder.append("://");
        builder.append(commentsApiProperties.getHost());
        appendPortToUrlIfNotEmpty(port, builder);
        builder.append(commentsApiProperties.getContextPath());
        builder.append("/");
        builder.append(API_BASE_PATH);
        builder.append("/");
        builder.append(API_VERSION);
        builder.append("/");
        builder.append(apiPath);
        builder.append("/");
        if (resourceId != null) {
            builder.append(resourceId);
        }
        return builder.toString();
    }

    private void appendPortToUrlIfNotEmpty(final String port,
                                           final StringBuilder builder) {
        if (port != null && !port.isEmpty()) {
            builder.append(":");
            builder.append(port);
        }
    }
}
