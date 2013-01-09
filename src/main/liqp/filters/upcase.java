package liqp.filters;

class upcase extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        return String.valueOf(value).toUpperCase();
    }
}
