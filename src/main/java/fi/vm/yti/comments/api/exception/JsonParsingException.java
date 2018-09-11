package fi.vm.yti.comments.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fi.vm.yti.comments.api.error.ErrorModel;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class JsonParsingException extends YtiCommentsException {

    public JsonParsingException(final String errorMessage) {
        super(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), errorMessage));
    }
}
