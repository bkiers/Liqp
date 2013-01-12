package liqp.filters;

import java.util.regex.Pattern;

class Split extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String original = String.valueOf(value);

        String delimiter = String.valueOf(super.get(0, params));

        return original.split(Pattern.quote(delimiter));
    }
}
