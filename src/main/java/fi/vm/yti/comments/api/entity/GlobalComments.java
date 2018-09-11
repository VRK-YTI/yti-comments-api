package fi.vm.yti.comments.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "globalcomments")
@XmlRootElement
public class GlobalComments extends AbstractIdentifyableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime created;
    private Source source;
    private Set<Comment> comments;

    @Column(name = "created")
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    public Source getSource() {
        return source;
    }

    public void setSource(final Source source) {
        this.source = source;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "globalComments", cascade = CascadeType.ALL)
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(final Set<Comment> comments) {
        this.comments = comments;
    }
}
