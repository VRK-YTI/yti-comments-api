package fi.vm.yti.comments.api.resource.externalresources;

import javax.ws.rs.core.Response;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.vm.yti.comments.api.exception.NotFoundException;
import fi.vm.yti.comments.api.resource.AbstractBaseResource;

interface AbstractIntegrationResource extends AbstractBaseResource {

    String CONTAINERS = "Containers";

    String RESOURCES = "Resources";

    default Response fetchIntegrationContainerData(final String requestUrl,
                                                   final RestTemplate restTemplate) {
        return fetchIntegrationResources(requestUrl, CONTAINERS, restTemplate);
    }

    default Response fetchIntegrationResourceData(final String requestUrl,
                                                  final RestTemplate restTemplate) {
        return fetchIntegrationResources(requestUrl, RESOURCES, restTemplate);
    }

    default Response fetchIntegrationResources(final String requestUrl,
                                               final String objectType,
                                               final RestTemplate restTemplate) {
        final ResponseEntity response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return createResponse(objectType, MESSAGE_TYPE_GET_RESOURCES, parseResourcesFromResponse(response));
        } else {
            throw new NotFoundException();
        }
    }

}
