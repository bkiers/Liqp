package liqp.filters;

import liqp.TemplateContext;

public class Default extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (params == null || params.length == 0) {
            return value;
        }

        if (super.isFalsy(value, context)) {
            return params[0];
        }

        return value;
    }
}
