package fi.vm.yti.comments.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "comment")
@XmlRootElement
public class Comment extends AbstractIdentifyableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String resourceUri;
    private String resourceSuggestion;
    private UUID userId;
    private Comment relatedComment;
    private CommentRound commentRound;
    private String content;
    private String proposedStatus;
    private GlobalComments globalComments;
    private LocalDateTime created;

    @Column(name = "resourceuri")
    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(final String resourceUri) {
        this.resourceUri = resourceUri;
    }

    @Column(name = "resourcesuggestion")
    public String getResourceSuggestion() {
        return resourceSuggestion;
    }

    public void setResourceSuggestion(final String resourceSuggestion) {
        this.resourceSuggestion = resourceSuggestion;
    }

    @Column(name = "user_id")
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    @OneToOne
    @JoinColumn(name = "relatedcomment_id")
    public Comment getRelatedComment() {
        return relatedComment;
    }

    public void setRelatedComment(final Comment relatedComment) {
        this.relatedComment = relatedComment;
    }

    @ManyToOne
    @JoinColumn(name = "commentround_id")
    public CommentRound getCommentRound() {
        return commentRound;
    }

    public void setCommentRound(final CommentRound commentRound) {
        this.commentRound = commentRound;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Column(name = "proposedstatus")
    public String getProposedStatus() {
        return proposedStatus;
    }

    public void setProposedStatus(final String proposedStatus) {
        this.proposedStatus = proposedStatus;
    }

    @ManyToOne
    @JoinColumn(name = "globalcomments_id")
    public GlobalComments getGlobalComments() {
        return globalComments;
    }

    public void setGlobalComments(final GlobalComments globalComments) {
        this.globalComments = globalComments;
    }

    @Column(name = "created")
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }
}
