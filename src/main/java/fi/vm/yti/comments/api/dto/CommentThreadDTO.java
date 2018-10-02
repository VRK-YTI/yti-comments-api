package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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

@JsonFilter("commentThread")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "created", "user", "resourceUri", "label", "description", "proposedText", "proposedStatus", "comments", "commentRound" })
@ApiModel(value = "Source", description = "Source DTO that represents data for one single source.")
public class CommentThreadDTO extends AbstractIdentifyableDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private String resourceUri;
    private Map<String, String> label;
    private Map<String, String> description;
    private String proposedText;
    private String proposedStatus;
    private UserDTO user;
    private LocalDateTime created;
    private Set<CommentDTO> comments;
    private CommentRoundDTO commentRound;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(final String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(final Map<String, String> label) {
        this.label = label;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(final Map<String, String> description) {
        this.description = description;
    }

    public String getProposedText() {
        return proposedText;
    }

    public void setProposedText(final String proposedText) {
        this.proposedText = proposedText;
    }

    public String getProposedStatus() {
        return proposedStatus;
    }

    public void setProposedStatus(final String proposedStatus) {
        this.proposedStatus = proposedStatus;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(final UserDTO user) {
        this.user = user;
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

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(final Set<CommentDTO> comments) {
        this.comments = comments;
    }

    public CommentRoundDTO getCommentRound() {
        return commentRound;
    }

    public void setCommentRound(final CommentRoundDTO commentRound) {
        this.commentRound = commentRound;
    }
}
