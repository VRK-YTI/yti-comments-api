package fi.vm.yti.comments.api.resource.externalresources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.exception.UnauthorizedException;
import fi.vm.yti.comments.api.resource.AbstractBaseResource;
import fi.vm.yti.security.AuthenticatedUserProvider;
import fi.vm.yti.security.YtiUser;

interface AbstractIntegrationResource extends AbstractBaseResource {

    String CONTAINERS = "Containers";

    String RESOURCES = "Resources";

    default Response fetchIntegrationContainerData(final String requestUrl,
                                                   final RestTemplate restTemplate,
                                                   final HttpMethod httpMethod) {
        return fetchIntegrationResources(requestUrl, CONTAINERS, restTemplate, httpMethod, null);
    }

    default Response fetchIntegrationResources(final String requestUrl,
                                               final String objectType,
                                               final RestTemplate restTemplate,
                                               final HttpMethod httpMethod,
                                               final String requestBody) {
        final ResponseEntity response;
        if (requestBody == null) {
            response = restTemplate.exchange(requestUrl, httpMethod, null, String.class);
        } else {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
            response = restTemplate.exchange(requestUrl, httpMethod, requestEntity, String.class);
        }
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return createResponse(objectType, MESSAGE_TYPE_GET_RESOURCES, parseResourcesFromResponse(response));
        } else {
            throw new NotFoundException();
        }
    }

    default void checkUser(final AuthenticatedUserProvider authenticatedUserProvider) {
        final YtiUser user = authenticatedUserProvider.getUser();
        if (user.isAnonymous()) {
            throw new UnauthorizedException();
        }
    }
}
