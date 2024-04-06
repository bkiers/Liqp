package liqp.filters;

import liqp.TemplateContext;

public class H extends Filter {

    /*
     * h(input)
     *
     * Alias for: escape
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        return Filters.COMMON_FILTERS.get("escape").apply(value, context, params);
    }
}
