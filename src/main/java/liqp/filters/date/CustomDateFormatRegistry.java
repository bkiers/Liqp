package liqp.filters.date;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a storage of supported types of Date/Time/DateTime types.
 * Known types are static context.
 */
public class CustomDateFormatRegistry {

    // might be better storage for this will be tree,
    // so the subtypes will be properly handled
    // and parent type will not override child's one
    private static final List<CustomDateFormatSupport> supportedTypes = new ArrayList<>();

    public static void add(CustomDateFormatSupport supportThis) {
        supportedTypes.add(0, supportThis);
    }


    public static boolean isRegistered(CustomDateFormatSupport<?> typeSupport) {
        return supportedTypes.contains(typeSupport);
    }

    public static boolean isCustomDateType(Object value) {
        for (CustomDateFormatSupport el: supportedTypes) {
            if (el.support(value)) {
                return true;
            }
        }
        return false;
    }

    public static ZonedDateTime getFromCustomType(Object value) {
        for (CustomDateFormatSupport el: supportedTypes) {
            if (el.support(value)) {
                return el.getValue(value);
            }
        }
        throw new UnsupportedOperationException();
    }
}
