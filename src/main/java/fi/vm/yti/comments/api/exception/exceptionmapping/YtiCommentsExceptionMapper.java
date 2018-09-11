package fi.vm.yti.comments.api.exception.exceptionmapping;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import fi.vm.yti.comments.api.exception.YtiCommentsException;

@Provider
public class YtiCommentsExceptionMapper implements BaseExceptionMapper, ExceptionMapper<YtiCommentsException> {

    @Override
    public Response toResponse(final YtiCommentsException e) {
        return getResponse(e);
    }
}

