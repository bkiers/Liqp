package liqp.filters;

import java.math.BigDecimal;

import liqp.PlainBigDecimal;

public class Minus extends Filter {

    /*
     * minus(input, operand)
     *
     * subtraction
     */
    @Override
    public Object apply(Object value, Object... params) {

        if (!isNumber(value)) {
            value = 0;
        }

        super.checkParams(params, 1);

        Object rhsObj = params[0];

        if (super.canBeInteger(value) && super.canBeInteger(rhsObj)) {
            return super.asNumber(value).longValue() - super.asNumber(rhsObj).longValue();
        }

        BigDecimal first = new PlainBigDecimal(super.asNumber(value).toString());
        BigDecimal second = new PlainBigDecimal(super.asNumber(rhsObj).toString());
        return asFormattedNumber(first.subtract(second));
    }
}
