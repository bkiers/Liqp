package liqp.filters;

import liqp.Template;
import liqp.TemplateContext;

public class H extends Filter {

    /*
     * h(input)
     *
     * Alias for: escape
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        return Filter.getFilter("escape").apply(value, context, params);
    }
}
