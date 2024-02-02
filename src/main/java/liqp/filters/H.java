package liqp.filters;

import liqp.TemplateContext;

public class H extends Filter {

    /*
     * h(input)
     *
     * Alias for: escape
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {
        return Filters.COMMON_FILTERS.get("escape").apply(context, value, params);
    }
}
