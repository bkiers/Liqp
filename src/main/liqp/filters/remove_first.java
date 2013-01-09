package liqp.filters;

import java.util.regex.Pattern;

class remove_first extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = String.valueOf(value);

        String needle = String.valueOf(super.get(0, params));

        return original.replaceFirst(Pattern.quote(needle), "");
    }
}
