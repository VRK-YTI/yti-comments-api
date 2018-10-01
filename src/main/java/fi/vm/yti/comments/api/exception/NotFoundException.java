package fi.vm.yti.comments.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.comments.api.error.ErrorModel;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_USER_404;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends YtiCommentsException {

    public NotFoundException() {
        super(new ErrorModel(HttpStatus.NOT_FOUND.value(), ERR_MSG_USER_404));
    }
}
