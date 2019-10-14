package fi.vm.yti.comments.api.dto;

public class IntegrationResourceRequestDTO extends AbstractBaseIntegrationRequestDTO {

    private String container;

    public String getContainer() {
        return container;
    }

    public void setContainer(final String container) {
        this.container = container;
    }

    private boolean includeIncomplete;

    public boolean isIncludeIncomplete() {
        return includeIncomplete;
    }

    public void setIncludeIncomplete(final boolean includeIncomplete) {
        this.includeIncomplete = includeIncomplete;
    }
}
