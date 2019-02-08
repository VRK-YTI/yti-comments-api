package fi.vm.yti.comments.api.utils;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import fi.vm.yti.comments.api.error.ErrorModel;
import fi.vm.yti.comments.api.exception.YtiCommentsException;
import static fi.vm.yti.comments.api.exception.ErrorConstants.ERR_MSG_INVALID_ID;

public class StringUtils {

    private static final Logger LOG = LoggerFactory.getLogger(StringUtils.class);

    public static UUID parseUUIDFromString(final String uuidString) {
        final UUID uuid;
        if (uuidString == null || uuidString.isEmpty()) {
            uuid = null;
        } else {
            try {
                uuid = UUID.fromString(uuidString);
            } catch (final IllegalArgumentException e) {
                LOG.error("UUID parsing failed from: " + uuidString, e);
                throw new YtiCommentsException(new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(), ERR_MSG_INVALID_ID));
            }
        }
        return uuid;
    }
}
