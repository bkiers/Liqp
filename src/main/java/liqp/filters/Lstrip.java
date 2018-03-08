package liqp.filters;

public class Lstrip extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        if (!super.isString(value)) {
            return value;
        }

        return super.asString(value).replaceAll("^\\s+", "");
    }
}
