package liqp.filters;

import liqp.TemplateContext;

public class Strip_Newlines extends Filter {

    /*
     * strip_newlines(input) click to toggle source
     *
     * Remove all newlines from the string
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        return super.asString(value, context).replaceAll("[\r\n]++", "");
    }
}
