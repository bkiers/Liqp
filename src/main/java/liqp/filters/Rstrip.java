package liqp.filters;

import liqp.TemplateContext;

public class Rstrip extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (!super.isString(value)) {
            return value;
        }

        return super.asString(value, context).replaceAll("\\s+$", "");
    }
}
