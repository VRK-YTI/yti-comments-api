package fi.vm.yti.comments.api.utils;

import java.util.UUID;

public interface StringUtils {

    static UUID parseUuidFromString(final String string) {
        try {
            return UUID.fromString(string);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    static Integer parseIntegerFromString(final String string) {
        try {
            return Integer.parseInt(string);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

}
