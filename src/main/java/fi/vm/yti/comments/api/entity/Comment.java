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

    private UUID userId;
    private Comment parentComment;
    private CommentThread commentThread;
    private String content;
    private String proposedStatus;
    private String endStatus;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String uri;
    private Integer sequenceId;

    @Column(name = "user_id")
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    @OneToOne
    @JoinColumn(name = "parentcomment_id")
    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(final Comment parentComment) {
        this.parentComment = parentComment;
    }

    @ManyToOne
    @JoinColumn(name = "commentthread_id")
    public CommentThread getCommentThread() {
        return commentThread;
    }

    public void setCommentThread(final CommentThread commentThread) {
        this.commentThread = commentThread;
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

    @Column(name = "endstatus")
    public String getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(final String endStatus) {
        this.endStatus = endStatus;
    }

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
