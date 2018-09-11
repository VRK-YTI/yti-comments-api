package fi.vm.yti.comments.api.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractTimeStampedIdentifyableEntity extends AbstractIdentifyableEntity {

    private LocalDateTime created;
    private LocalDateTime modified;

    @Column(name = "created")
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    @Column(name = "modified")
    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(final LocalDateTime modified) {
        this.modified = modified;
    }
}
