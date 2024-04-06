package liqp.filters;

import liqp.TemplateContext;

public class Ceil extends Filter {

    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (!super.isNumber(value)) {
            return value;
        }

        return (long)Math.ceil(super.asNumber(value).doubleValue());
    }
}
