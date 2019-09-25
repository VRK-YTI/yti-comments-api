package fi.vm.yti.comments.api.dto;

public class IntegrationResourceRequestDTO extends AbstractBaseIntegrationRequestDTO {

    private String container;

    public String getContainer() {
        return container;
    }

    public void setContainer(final String container) {
        this.container = container;
    }
}
