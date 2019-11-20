package fi.vm.yti.comments.api.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import fi.vm.yti.comments.api.error.Meta;

public class AbstractService {

    static final int MAX_PAGE_SIZE = 50000;
    static final String FIELD_SEQUENCE_ID = "sequenceId";

    int getPageIndex(final Meta meta) {
        final Integer from = meta.getFrom();
        final Integer pageSize = meta.getPageSize();
        if (from != null && from > 0 && pageSize != null) {
            return from / pageSize;
        }
        return 0;
    }

    LocalDateTime convertDateToLocalDateTime(final Date date) {
        if (date != null) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }
}
