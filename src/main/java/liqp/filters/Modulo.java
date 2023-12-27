package liqp.filters;

import java.math.BigDecimal;

import liqp.PlainBigDecimal;
import liqp.TemplateContext;

public class Modulo extends Filter {

    /*
     * modulo(input, operand)
     *
     * modulus
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if(value == null) {
            value = 0L;
        }

        super.checkParams(params, 1);

        Object rhsObj = params[0];

        if (super.canBeInteger(value) && super.canBeInteger(rhsObj)) {
            return super.asNumber(value).longValue() % super.asNumber(rhsObj).longValue();
        }

        BigDecimal first = new PlainBigDecimal(super.asNumber(value).toString());
        BigDecimal second = new PlainBigDecimal(super.asNumber(rhsObj).toString());
        return asFormattedNumber(first.remainder(second));
    }
}
