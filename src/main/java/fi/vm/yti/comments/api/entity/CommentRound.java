package fi.vm.yti.comments.api.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "commentround")
@XmlRootElement
public class CommentRound extends AbstractTimeStampedIdentifyableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate startDate;
    private LocalDate endDate;
    private UUID userId;
    private String label;
    private String description;
    private Source source;
    private Set<Comment> comments;

    @Column(name = "startdate")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = "enddate")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    @Column(name = "user_id")
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    @Column(name = "label")
    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    public Source getSource() {
        return source;
    }

    public void setSource(final Source source) {
        this.source = source;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "commentRound", cascade = CascadeType.ALL)
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(final Set<Comment> comments) {
        this.comments = comments;
    }
}
