package fi.vm.yti.comments.api.exception;

import fi.vm.yti.comments.api.error.ErrorModel;

public class YtiCommentsException extends RuntimeException {

    private final ErrorModel errorModel;

    public YtiCommentsException(final ErrorModel errorModel) {
        super(errorModel.getMessage());
        this.errorModel = errorModel;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }
}
