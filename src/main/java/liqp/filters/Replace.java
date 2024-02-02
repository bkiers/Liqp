package liqp.filters;

import liqp.TemplateContext;

public class Replace extends Filter {

    /*
     * replace(input, string, replacement = '')
     *
     * Replace occurrences of a string with another
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        String original = super.asString(value, context);

        Object needle = super.get(0, params);
        String replacement = "";

        if (needle == null) {
            throw new RuntimeException("invalid pattern: " + needle);
        }

        if (params.length >= 2) {

            Object obj = super.get(1, params);

            if (obj == null) {
                throw new RuntimeException("invalid replacement: " + needle);
            }

            replacement = super.asString(super.get(1, params), context);
        }

        return original.replace(String.valueOf(needle), String.valueOf(replacement));
    }
}