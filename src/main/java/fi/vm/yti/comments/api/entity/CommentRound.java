package fi.vm.yti.comments.api.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
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
    private String sourceLocalName;
    private String status;
    private Source source;
    private boolean fixedThreads;
    private boolean openThreads;
    private Set<Organization> organizations;
    private Map<String, String> sourceLabel;
    private Set<CommentThread> commentThreads;
    private LocalDateTime contentModified;
    private LocalDateTime statusModified;
    private String uri;
    private Integer sequenceId;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
    @JoinColumn(name = "source_id", nullable = false)
    public Source getSource() {
        return source;
    }

    public void setSource(final Source source) {
        this.source = source;
    }

    @Column(name = "sourcelocalname")
    public String getSourceLocalName() {
        return sourceLocalName;
    }

    public void setSourceLocalName(final String sourceLocalName) {
        this.sourceLocalName = sourceLocalName;
    }

    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Column(name = "fixedthreads")
    public boolean getFixedThreads() {
        return fixedThreads;
    }

    public void setFixedThreads(final boolean fixedThreads) {
        this.fixedThreads = fixedThreads;
    }

    @Column(name = "openthreads")
    public boolean getOpenThreads() {
        return openThreads;
    }

    public void setOpenThreads(final boolean openThreads) {
        this.openThreads = openThreads;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name = "commentround_organization",
        joinColumns = {
            @JoinColumn(name = "commentround_id", referencedColumnName = "id") },
        inverseJoinColumns = {
            @JoinColumn(name = "organization_id", referencedColumnName = "id") })
    public Set<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(final Set<Organization> organizations) {
        this.organizations = organizations;
    }

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "commentround_sourcelabel", joinColumns = @JoinColumn(name = "commentround_id", referencedColumnName = "id"))
    @MapKeyColumn(name = "language")
    @Column(name = "sourcelabel")
    @OrderColumn
    public Map<String, String> getSourceLabel() {
        return sourceLabel;
    }

    public void setSourceLabel(final Map<String, String> sourceLabel) {
        this.sourceLabel = sourceLabel;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "commentRound", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    public Set<CommentThread> getCommentThreads() {
        return commentThreads;
    }

    public void setCommentThreads(final Set<CommentThread> commentThreads) {
        this.commentThreads = commentThreads;
    }

    @Column(name = "content_modified")
    public LocalDateTime getContentModified() {
        return contentModified;
    }

    public void setContentModified(final LocalDateTime contentModified) {
        this.contentModified = contentModified;
    }

    @Column(name = "status_modified")
    public LocalDateTime getStatusModified() {
        return statusModified;
    }

    public void setStatusModified(final LocalDateTime statusModified) {
        this.statusModified = statusModified;
    }

    @Column(name = "uri")
    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Column(name = "sequence_id")
    public Integer getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(final Integer sequenceId) {
        this.sequenceId = sequenceId;
    }
}
