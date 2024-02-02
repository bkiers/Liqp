package liqp.filters;

import liqp.TemplateContext;

import java.math.BigDecimal;

import liqp.PlainBigDecimal;

public class Times extends Filter {

    /*
     * times(input, operand)
     *
     * multiplication
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        if(value == null) {
            value = 0L;
        }

        super.checkParams(params, 1);

        Object rhsObj = params[0];

        if (super.canBeInteger(value) && super.canBeInteger(rhsObj)) {
            return super.asNumber(value).longValue() * super.asNumber(rhsObj).longValue();
        }

        BigDecimal first = new PlainBigDecimal(super.asNumber(value).toString());
        BigDecimal second = new PlainBigDecimal(super.asNumber(rhsObj).toString());
        return new PlainBigDecimal(first.multiply(second));
    }
}
