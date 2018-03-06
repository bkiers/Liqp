package liqp.filters;

public class Floor extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if (!super.isNumber(value)) {
            return value;
        }

        return (long)Math.floor(super.asNumber(value).doubleValue());
    }
}
