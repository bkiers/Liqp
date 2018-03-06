package liqp.filters;

public class Ceil extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if (!super.isNumber(value)) {
            return value;
        }

        return (long)Math.ceil(super.asNumber(value).doubleValue());
    }
}
