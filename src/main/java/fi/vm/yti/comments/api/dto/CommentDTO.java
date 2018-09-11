package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;

import io.swagger.annotations.ApiModel;

@JsonFilter("comment")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "created", "modified", "resourceUri", "userId", "relatedComment", "content", "globalComments" })
@ApiModel(value = "Comment", description = "Comment entity that represents data for one single comment.")
public class CommentDTO extends AbstractTimeStampedIdentifyableEntityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private String resourceUri;
    private UUID userId;
    private CommentDTO relatedComment;
    private CommentRoundDTO commentRound;
    private String content;
    private String proposedStatus;
    private GlobalCommentsDTO globalComments;

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(final String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    public CommentDTO getRelatedComment() {
        return relatedComment;
    }

    public void setRelatedComment(final CommentDTO relatedComment) {
        this.relatedComment = relatedComment;
    }

    public CommentRoundDTO getCommentRound() {
        return commentRound;
    }

    public void setCommentRound(final CommentRoundDTO commentRound) {
        this.commentRound = commentRound;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getProposedStatus() {
        return proposedStatus;
    }

    public void setProposedStatus(final String proposedStatus) {
        this.proposedStatus = proposedStatus;
    }

    public GlobalCommentsDTO getGlobalComments() {
        return globalComments;
    }

    public void setGlobalComments(final GlobalCommentsDTO globalComments) {
        this.globalComments = globalComments;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
