package liqp.filters;

import liqp.TemplateContext;

public class Divided_By extends Filter {

    /*
     * divided_by(input, operand)
     *
     * division
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if(value == null) {
            value = 0L;
        }

        super.checkParams(params, 1);

        Object rhsObj = params[0];

        if (super.canBeInteger(value) && super.canBeInteger(rhsObj)) {
            return super.asNumber(value).longValue() / super.asNumber(rhsObj).longValue();
        }

        return super.asNumber(value).doubleValue() / super.asNumber(rhsObj).doubleValue();
    }
}
