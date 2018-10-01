package fi.vm.yti.comments.api.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "source")
@XmlRootElement
public class Source extends AbstractIdentifyableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String containerType;
    private String containerUri;

    @Column(name = "containertype")
    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(final String containerType) {
        this.containerType = containerType;
    }

    @Column(name = "containeruri")
    public String getContainerUri() {
        return containerUri;
    }

    public void setContainerUri(final String containerUri) {
        this.containerUri = containerUri;
    }
}
