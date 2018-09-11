package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonFilter("commentRound")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "created", "modified", "startDate", "endDate", "comments"})
@ApiModel(value = "CommentRound", description = "CommentRound entity that represents data for one single comment round.")
public class CommentRoundDTO extends AbstractTimeStampedIdentifyableEntityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID userId;
    private String label;
    private String description;
    private SourceDTO source;
    private Set<CommentDTO> comments;

    @ApiModelProperty(dataType = "dateTime")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @ApiModelProperty(dataType = "dateTime")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public SourceDTO getSource() {
        return source;
    }

    public void setSource(final SourceDTO source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Set<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(final Set<CommentDTO> comments) {
        this.comments = comments;
    }
}
