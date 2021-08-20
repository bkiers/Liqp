package liqp.filters;

import liqp.TemplateContext;

public class Capitalize extends Filter {

    /*
     * (Object) capitalize(input)
     *
     * capitalize words in the input sentence
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        String original = super.asString(value, context);

        if (original.isEmpty()) {
            return original;
        }

        char first = original.charAt(0);

        return Character.toUpperCase(first) + original.substring(1);
    }
}
