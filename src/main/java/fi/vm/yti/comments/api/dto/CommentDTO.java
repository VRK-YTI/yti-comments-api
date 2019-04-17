package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@XmlType(propOrder = { "id", "url", "created", "user", "content", "proposedStatus", "endStatus", "parentComment", "commentThread" })
@ApiModel(value = "Comment", description = "Comment DTO that represents data for one single comment in a commmentThread.")
public class CommentDTO extends AbstractIdentifyableDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private UserDTO user;
    private CommentDTO parentComment;
    private CommentThreadDTO commmentThread;
    private String content;
    private String proposedStatus;
    private String endStatus;
    private LocalDateTime created;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(final UserDTO user) {
        this.user = user;
    }

    public CommentDTO getParentComment() {
        return parentComment;
    }

    public void setParentComment(final CommentDTO parentComment) {
        this.parentComment = parentComment;
    }

    public CommentThreadDTO getCommentThread() {
        return commmentThread;
    }

    public void setCommentThread(final CommentThreadDTO commentThread) {
        this.commmentThread = commentThread;
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

    public String getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(final String endStatus) {
        this.endStatus = endStatus;
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
