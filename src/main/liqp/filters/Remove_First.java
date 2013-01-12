package liqp.filters;

import java.util.regex.Pattern;

class Remove_First extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = super.asString(value);

        Object needle = super.get(0, params);

        if(needle == null) {
            throw new RuntimeException("invalid pattern: " + needle);
        }

        return original.replaceFirst(Pattern.quote(String.valueOf(needle)), "");
    }
}
