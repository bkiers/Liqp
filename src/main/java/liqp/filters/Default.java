package liqp.filters;

public class Default extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if (params == null || params.length == 0) {
            return value;
        }

        if (super.isFalsy(value)) {
            return params[0];
        }

        return value;
    }
}
