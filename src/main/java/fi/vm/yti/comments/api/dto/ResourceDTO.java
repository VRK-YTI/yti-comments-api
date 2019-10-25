package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@XmlType(propOrder = { "uri", "prefLabel", "description", "type", "localName", "status", "modified", "contentModified", "statusModified" })
@Schema(name = "Resource", description = "Resource DTO that represents data for one single Container or Resource for integration use.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> prefLabel;
    private Map<String, String> description;
    private String type;
    private String localName;
    private String uri;
    private String status;
    private Date modified;
    private Date contentModified;
    private Date statusModified;

    public ResourceDTO() {
        prefLabel = new HashMap<>();
        description = new HashMap<>();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public Map<String, String> getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(final Map<String, String> prefLabel) {
        this.prefLabel = prefLabel;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(final Map<String, String> description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(final String localName) {
        this.localName = localName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Schema(format = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date getModified() {
        return modified;
    }

    public void setModified(final Date modified) {
        this.modified = modified;
    }

    @Schema(format = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date getContentModified() {
        return contentModified;
    }

    public void setContentModified(final Date contentModified) {
        this.contentModified = contentModified;
    }

    @Schema(format = "dateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public Date getStatusModified() {
        return statusModified;
    }

    public void setStatusModified(final Date statusModified) {
        this.statusModified = statusModified;
    }
}