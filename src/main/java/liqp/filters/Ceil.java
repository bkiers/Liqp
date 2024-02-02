package liqp.filters;

import liqp.TemplateContext;

public class Ceil extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if (!super.isNumber(value)) {
            return value;
        }

        return (long)Math.ceil(super.asNumber(value).doubleValue());
    }
}
