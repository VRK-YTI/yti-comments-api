package fi.vm.yti.comments.api.dto;

public class SystemMetaCountDTO {

    private long commentRoundCount;
    private long commentThreadCount;
    private long commentCount;

    public SystemMetaCountDTO(final long commentRoundCount,
                              final long commentThreadCount,
                              final long commentCount) {
        this.commentRoundCount = commentRoundCount;
        this.commentThreadCount = commentThreadCount;
        this.commentCount = commentCount;
    }

    public long getCommentRoundCount() {
        return commentRoundCount;
    }

    public long getCommentThreadCount() {
        return commentThreadCount;
    }

    public long getCommentCount() {
        return commentCount;
    }
}
