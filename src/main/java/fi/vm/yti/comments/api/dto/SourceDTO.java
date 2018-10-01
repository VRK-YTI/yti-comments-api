package fi.vm.yti.comments.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;

import io.swagger.annotations.ApiModel;

@JsonFilter("source")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "containerType", "containerUri" })
@ApiModel(value = "Source", description = "Source DTO that represents data for one single source.")
public class SourceDTO extends AbstractIdentifyableDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private String containerType;
    private String containerUri;

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(final String containerType) {
        this.containerType = containerType;
    }

    public String getContainerUri() {
        return containerUri;
    }

    public void setContainerUri(final String containerUri) {
        this.containerUri = containerUri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
