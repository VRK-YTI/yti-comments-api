package fi.vm.yti.comments.api.dto;

import java.util.List;

public class IntegrationResourceRequestDTO extends AbstractBaseIntegrationRequestDTO {

    private String container;
    private boolean includeIncomplete;
    private List<String> includeIncompleteFrom;
    private List<String> uri;

    public String getContainer() {
        return container;
    }

    public void setContainer(final String container) {
        this.container = container;
    }

    public boolean getIncludeIncomplete() {
        return includeIncomplete;
    }

    public void setIncludeIncomplete(final boolean includeIncomplete) {
        this.includeIncomplete = includeIncomplete;
    }

    public List<String> getIncludeIncompleteFrom() {
        return includeIncompleteFrom;
    }

    public void setIncludeIncompleteFrom(final List<String> includeIncompleteFrom) {
        this.includeIncompleteFrom = includeIncompleteFrom;
    }

    public List<String> getUri() {
        return uri;
    }

    public void setUri(final List<String> uri) {
        this.uri = uri;
    }
}
