package fi.vm.yti.comments.api.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public interface DateUtils {

    static Date convertLocalDateTimeToDate(final LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }
}
