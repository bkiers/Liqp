package liqp.filters;

public class At_Least extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if (params == null || params.length == 0) {
            return value;
        }

        if (!super.isNumber(value) || !super.isNumber(params[0])) {
            return value;
        }

        Number numberValue = super.asNumber(value);
        Number paramValue = super.asNumber(params[0]);

        if (numberValue.doubleValue() < paramValue.doubleValue()) {
            return paramValue;
        }

        return numberValue;
    }
}
