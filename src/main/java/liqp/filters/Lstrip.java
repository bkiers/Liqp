package liqp.filters;

import liqp.TemplateContext;

public class Lstrip extends Filter {

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (!super.isString(value)) {
            return value;
        }

        return super.asString(value, context).replaceAll("^\\s+", "");
    }
}
