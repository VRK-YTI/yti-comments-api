package fi.vm.yti.comments.api.error;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import fi.vm.yti.comments.api.exception.YtiCommentsException;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlType(propOrder = { "code", "message", "pageSize", "from", "resultCount", "totalResults", "after", "afterResourceUrl", "nextPage" })
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "Meta", description = "Meta information model for API responses.")
public class Meta {

    private static final Logger LOG = LoggerFactory.getLogger(Meta.class);

    private Integer code;
    private String message;
    private Integer pageSize;
    private Integer from;
    private Integer resultCount;
    private Integer totalResults;
    private Date after;
    private Date before;
    private String afterResourceUrl;
    private String nextPage;
    private String entityIdentifier;

    public Meta() {
    }

    public Meta(final Integer code,
                final Integer pageSize,
                final Integer from,
                final String after,
                final String before) {
        this.code = code;
        this.pageSize = pageSize;
        this.from = from;
        this.after = parseDateFromString(after);
        this.before = parseDateFromString(before);
    }

    public Meta(final Integer code,
                final Integer pageSize,
                final Integer from,
                final String after,
                final String before,
                final String entityIdentifier) {
        this.code = code;
        this.pageSize = pageSize;
        this.from = from;
        this.after = parseDateFromString(after);
        this.before = parseDateFromString(before);
        this.entityIdentifier = entityIdentifier;
    }

    public static Date parseDateFromString(final String dateString) {
        if (dateString != null) {
            final ISO8601DateFormat dateFormat = new ISO8601DateFormat();
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                LOG.error(String.format("Parsing date from string failed: " + dateString));
                throw new YtiCommentsException(new ErrorModel(HttpStatus.BAD_REQUEST.value(), "Date input not valid: " + dateString));
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(final Integer from) {
        this.from = from;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(final Integer resultCount) {
        this.resultCount = resultCount;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Date getAfter() {
        if (after != null) {
            return new Date(after.getTime());
        }
        return null;
    }

    public void setAfter(final Date after) {
        if (after != null) {
            this.after = new Date(after.getTime());
        } else {
            this.after = null;
        }
    }

    public String getAfterResourceUrl() {
        return afterResourceUrl;
    }

    public void setAfterResourceUrl(final String afterResourceUrl) {
        this.afterResourceUrl = afterResourceUrl;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(final String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }

    public Date getBefore() {
        return before;
    }

    public void setBefore(final Date before) {
        this.before = before;
    }
}
