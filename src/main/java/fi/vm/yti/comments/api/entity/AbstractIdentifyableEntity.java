package fi.vm.yti.comments.api.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractIdentifyableEntity {

    private UUID id;

    @Id
    @Column(name = "id", unique = true)
    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }
}
