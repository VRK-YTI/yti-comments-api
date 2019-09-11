package fi.vm.yti.comments.api.dto;

public class SystemMetaCountDTO {

    private Long commentRoundCount;
    private Long commentThreadCount;
    private Long commentCount;

    public SystemMetaCountDTO(final Long commentRoundCount,
                              final Long commentThreadCount,
                              final Long commentCount) {
        this.commentRoundCount = commentRoundCount;
        this.commentThreadCount = commentThreadCount;
        this.commentCount = commentCount;
    }

    public Long getCommentRoundCount() {
        return commentRoundCount;
    }

    public Long getCommentThreadCount() {
        return commentThreadCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }
}
