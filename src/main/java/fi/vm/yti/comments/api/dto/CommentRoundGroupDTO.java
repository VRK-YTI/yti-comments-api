package fi.vm.yti.comments.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonFilter;

import io.swagger.annotations.ApiModel;

@JsonFilter("commentRoundGroup")
@XmlRootElement
@XmlType(propOrder = { "id", "url", "commentRound" })
@ApiModel(value = "CommentRoundGroup", description = "CommentRoundGroup DTO that represents data for one single comment round group.")
public class CommentRoundGroupDTO extends AbstractIdentifyableEntityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    private CommentRoundDTO commentRound;

    public CommentRoundDTO getCommentRound() {
        return commentRound;
    }

    public void setCommentRound(final CommentRoundDTO commentRound) {
        this.commentRound = commentRound;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
