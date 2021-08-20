package liqp.filters;

import liqp.TemplateContext;

public class Upcase extends Filter {

    /*
     * upcase(input)
     *
     * convert a input string to UPCASE
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        return super.asString(value, context).toUpperCase();
    }
}
