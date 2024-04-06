package liqp.filters;

import liqp.TemplateContext;

public class Floor extends Filter {

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (!super.isNumber(value)) {
            return value;
        }

        return (long)Math.floor(super.asNumber(value).doubleValue());
    }
}
