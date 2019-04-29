package fi.vm.yti.comments.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@XmlType(propOrder = { "status", "count" })
@ApiModel(value = "CommentThreadResult", description = "CommentThread result DTO that represents comment result data for one single comment thread.")
public class CommentThreadResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private Integer count;
    private String percentage;

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(final String percentage) {
        this.percentage = percentage;
    }
}
