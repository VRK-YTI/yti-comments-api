package fi.vm.yti.comments.api.error;

import java.io.Serializable;

public class ErrorModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private int httpStatusCode;
    private String message;

    private String entityIdentifier;

    public ErrorModel(final int httpStatusCode,
                      final String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public ErrorModel(final int httpStatusCode,
                      final String message,
                      final String entityIdentifier) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        this.entityIdentifier = entityIdentifier;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(final int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getEntityIdentifier() {
        return entityIdentifier;
    }

    public void setEntityIdentifier(final String entityIdentifier) {
        this.entityIdentifier = entityIdentifier;
    }
}
