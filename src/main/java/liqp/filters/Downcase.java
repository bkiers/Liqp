package liqp.filters;

import liqp.TemplateContext;

public class Downcase extends Filter {

    /*
     * downcase(input)
     *
     * convert a input string to DOWNCASE
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        return super.asString(value, context).toLowerCase();
    }
}
