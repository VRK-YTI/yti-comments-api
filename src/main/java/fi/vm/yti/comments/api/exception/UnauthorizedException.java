package fi.vm.yti.comments.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.comments.api.error.ErrorModel;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class UnauthorizedException extends YtiCommentsException {

    public UnauthorizedException(final ErrorModel errorModel) {
        super(errorModel);
    }
}
