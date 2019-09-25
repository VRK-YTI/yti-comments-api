package fi.vm.yti.comments.api.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import static fi.vm.yti.comments.api.constants.ApiConstants.LANGUAGE_CODE_EN;

@JsonFilter("organization")
@XmlType(propOrder = { "id", "url", "prefLabel", "description", "removed", "commentRounds" })
@Schema(name = "Organization", description = "Organization DTO that represents data for one single Organization.")
public class OrganizationDTO extends AbstractIdentifyableDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private Boolean removed;
    private Map<String, String> prefLabel;
    private Map<String, String> description;
    private Set<CommentRoundDTO> commentRounds;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(final Boolean removed) {
        this.removed = removed;
    }

    public Map<String, String> getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(final Map<String, String> prefLabel) {
        this.prefLabel = prefLabel;
    }

    public String getPrefLabel(final String language) {
        String prefLabelValue = this.prefLabel.get(language);
        if (prefLabelValue == null) {
            prefLabelValue = this.prefLabel.get(LANGUAGE_CODE_EN);
        }
        return prefLabelValue;
    }

    public void setPrefLabel(final String language,
                             final String value) {
        if (this.prefLabel == null) {
            this.prefLabel = new HashMap<>();
        }
        if (language != null && value != null && !value.isEmpty()) {
            this.prefLabel.put(language, value);
        } else if (language != null) {
            this.prefLabel.remove(language);
        }
        setPrefLabel(this.prefLabel);
    }

    public Map<String, String> getDescription() {
        if (description == null) {
            description = new HashMap<>();
        }
        return description;
    }

    public void setDescription(final Map<String, String> description) {
        this.description = description;
    }

    public String getDescription(final String language) {
        String descriptionValue = this.description.get(language);
        if (descriptionValue == null) {
            descriptionValue = this.description.get(LANGUAGE_CODE_EN);
        }
        return descriptionValue;
    }

    public void setDescription(final String language,
                               final String value) {
        if (this.description == null) {
            this.description = new HashMap<>();
        }
        if (language != null && value != null && !value.isEmpty()) {
            this.description.put(language, value);
        } else if (language != null) {
            this.description.remove(language);
        }
        setDescription(this.description);
    }

    public Set<CommentRoundDTO> getCommentRounds() {
        return commentRounds;
    }

    public void setCommentRounds(final Set<CommentRoundDTO> commentRounds) {
        this.commentRounds = commentRounds;
    }
}
