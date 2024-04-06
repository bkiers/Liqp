package liqp.filters;

import liqp.TemplateContext;

import java.util.regex.Pattern;

public class Split extends Filter {

    /*
     * split(input, delimiter = ' ')
     *
     * Split a string on a matching pattern
     *
     * E.g. {{ "a~b" | split:'~' | first }} #=> 'a'
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        String original = super.asString(value, context);
        if (original.isEmpty()) {
            return new String[0];
        }

        String delimiter = super.asString(super.get(0, params), context);

        String[] val = original.split("(?<!^)" + Pattern.quote(delimiter));

        return val;
    }
}
