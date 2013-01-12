package liqp.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Replace_First extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = super.asString(value);

        Object needle = super.get(0, params);
        Object replacement = super.get(1, params);

        if(needle == null) {
            throw new RuntimeException("invalid pattern: " + needle);
        }

        if(replacement == null) {
            throw new RuntimeException("invalid replacement: " + needle);
        }

        return original.replaceFirst(Pattern.quote(String.valueOf(needle)),
                Matcher.quoteReplacement(String.valueOf(replacement)));
    }
}
