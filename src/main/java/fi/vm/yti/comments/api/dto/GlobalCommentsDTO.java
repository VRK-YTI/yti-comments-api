package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
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

@JsonFilter("globalComments")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "created", "source", "comments" })
@ApiModel(value = "GlobalComments", description = "GlobalComments DTO that represents data for one single globalComments.")
public class GlobalCommentsDTO extends AbstractIdentifyableEntityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private LocalDateTime created;
    private SourceDTO source;
    private Set<CommentDTO> comments;

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
