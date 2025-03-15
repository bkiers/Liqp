package liqp.filters.date;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class ArrayToRegexp implements BiFunction<String[], Locale, String> {

    @Override
    public String apply(String[] strings, Locale locale) {
        String[] values = withoutNulls(strings, locale);
        return String.join("|", values);
    }

    protected String[] withoutNulls(String[] enumValues, Locale locale) {
        return Arrays.stream(enumValues)
                .filter(val -> val != null && !val.isEmpty())
                .map(s -> s.toLowerCase(locale))
                .map(Pattern::quote)
                .toArray(String[]::new);
    }
}
