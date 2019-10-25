package fi.vm.yti.comments.api.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import fi.vm.yti.comments.api.error.Meta;

public class AbstractService {

    static final int MAX_PAGE_COUNT = 50000;

    int getPageIndex(final Meta meta) {
        final Integer from = meta.getFrom();
        final Integer pageSize = meta.getPageSize();
        if (from != null && pageSize != null) {
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
