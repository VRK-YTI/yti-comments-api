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

import io.swagger.v3.oas.annotations.media.Schema;

@JsonFilter("commentThread")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "uri", "sequenceId", "created", "user", "resourceUri", "label", "description", "localName", "proposedText", "currentStatus", "proposedStatus", "comments", "commentRound", "results", "commentCount" })
@Schema(name = "CommentThread", description = "CommentThread DTO that represents data for one single CommentThread.")
public class CommentThreadDTO extends AbstractIdentifyableDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private String resourceUri;
    private Map<String, String> label;
    private Map<String, String> description;
    private String localName;
    private String proposedText;
    private String currentStatus;
    private String proposedStatus;
    private UserDTO user;
    private LocalDateTime created;
    private Set<CommentDTO> comments;
    private CommentRoundDTO commentRound;
    private Set<CommentThreadResultDTO> results;
    private Integer commentCount;
    private String uri;
    private Integer sequenceId;

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

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(final String localName) {
        this.localName = localName;
    }

    public String getProposedText() {
        return proposedText;
    }

    public void setProposedText(final String proposedText) {
        this.proposedText = proposedText;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(final String currentStatus) {
        this.currentStatus = currentStatus;
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

    @Schema(format = "dateTime")
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

    public Set<CommentThreadResultDTO> getResults() {
        return results;
    }

    public void setResults(final Set<CommentThreadResultDTO> results) {
        this.results = results;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(final Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public Integer getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(final Integer sequenceId) {
        this.sequenceId = sequenceId;
    }
}
