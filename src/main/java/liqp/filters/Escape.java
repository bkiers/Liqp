package liqp.filters;

import liqp.TemplateContext;

public class Escape extends Filter {

    /*
     * escape(input)
     *
     * escape a string
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        String str = super.asString(value, context);

        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
