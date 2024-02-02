package liqp.filters;

import liqp.TemplateContext;

public class Newline_To_Br extends Filter {

    /*
     * newline_to_br(input)
     *
     * Add <br /> tags in front of all newlines in input string
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        return super.asString(value, context).replaceAll("[\n]", "<br />\n");
    }
}
