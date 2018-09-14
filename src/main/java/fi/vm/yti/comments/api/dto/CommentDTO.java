package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonFilter("comment")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "created", "resourceUri", "resourceSuggestion", "userId", "relatedComment", "content", "globalComments" })
@ApiModel(value = "Comment", description = "Comment entity that represents data for one single comment.")
public class CommentDTO extends AbstractIdentifyableEntityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private String resourceUri;
    private String resourceSuggestion;
    private UUID userId;
    private CommentDTO relatedComment;
    private CommentRoundDTO commentRound;
    private String content;
    private String proposedStatus;
    private GlobalCommentsDTO globalComments;
    private LocalDateTime created;

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(final String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getResourceSuggestion() {
        return resourceSuggestion;
    }

    public void setResourceSuggestion(final String resourceSuggestion) {
        this.resourceSuggestion = resourceSuggestion;
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

    @ApiModelProperty(dataType = "dateTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }
}
