package fi.vm.yti.comments.api.dto;

import java.util.UUID;

public class AbstractIdentifyableEntityDTO {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }
}
