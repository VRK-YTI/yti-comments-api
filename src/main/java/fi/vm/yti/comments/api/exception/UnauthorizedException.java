package fi.vm.yti.comments.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.comments.api.error.ErrorModel;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_401;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends YtiCommentsException {

    public UnauthorizedException() {
        super(new ErrorModel(HttpStatus.UNAUTHORIZED.value(), ERR_MSG_USER_401));
    }

    public UnauthorizedException(final ErrorModel errorModel) {
        super(errorModel);
    }
}
