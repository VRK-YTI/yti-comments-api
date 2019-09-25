package fi.vm.yti.comments.api.dto;

public class IntegrationContainerRequestDTO extends AbstractBaseIntegrationRequestDTO {

    private boolean includeIncomplete;

    public boolean isIncludeIncomplete() {
        return includeIncomplete;
    }

    public void setIncludeIncomplete(final boolean includeIncomplete) {
        this.includeIncomplete = includeIncomplete;
    }
}
