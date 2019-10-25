package fi.vm.yti.comments.api.dto;

import java.util.List;

public class AbstractBaseIntegrationRequestDTO {

    private Integer pageFrom;
    private Integer pageSize;
    private List<String> status;
    private String after;
    private String before;
    private List<String> filter;
    private String language;
    private String searchTerm;
    private boolean pretty;

    public Integer getPageFrom() {
        return pageFrom;
    }

    public void setPageFrom(final Integer pageFrom) {
        this.pageFrom = pageFrom;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(final List<String> status) {
        this.status = status;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(final String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(final String before) {
        this.before = before;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(final List<String> filter) {
        this.filter = filter;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean getPretty() {
        return pretty;
    }

    public void setPretty(final boolean pretty) {
        this.pretty = pretty;
    }
}
