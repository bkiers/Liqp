package liqp.filters;

public class Default extends Filter {

    @Override
    public Object apply(Object value, Object... params) {
        throw new RuntimeException("TODO: " + getClass().getSimpleName());
    }
}