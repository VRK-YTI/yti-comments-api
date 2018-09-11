package fi.vm.yti.comments.api.exception.exceptionmapping;

import javax.ws.rs.core.Response;

import fi.vm.yti.comments.api.api.ResponseWrapper;
import fi.vm.yti.comments.api.error.Meta;
import fi.vm.yti.comments.api.exception.YtiCommentsException;

interface BaseExceptionMapper {

    default Response getResponse(final YtiCommentsException e) {
        final ResponseWrapper wrapper = new ResponseWrapper();
        final Meta meta = new Meta();
        meta.setMessage(e.getErrorModel().getMessage());
        meta.setCode(e.getErrorModel().getHttpStatusCode());
        meta.setEntityIdentifier(e.getErrorModel().getEntityIdentifier());
        wrapper.setMeta(meta);
        return Response.status(e.getErrorModel().getHttpStatusCode()).entity(wrapper).build();
    }
}
