package liqp.filters;

import liqp.TemplateContext;

public class Remove extends Filter {

    /*
     * remove(input, string)
     *
     * remove a substring
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        String original = super.asString(value, context);

        Object needle = super.get(0, params);

        if (needle == null) {
            throw new RuntimeException("invalid pattern: " + needle);
        }

        return original.replace(String.valueOf(needle), "");
    }
}
