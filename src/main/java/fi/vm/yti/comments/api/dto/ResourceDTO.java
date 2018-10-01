package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@XmlType(propOrder = { "uri", "prefLabel", "description", "status" })
@ApiModel(value = "Resource", description = "Resource DTO that represents data for one single container or resource for integration use.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> prefLabel;
    private Map<String, String> description;
    private String uri;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}