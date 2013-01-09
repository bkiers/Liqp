package liqp.filters;

class downcase extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        return String.valueOf(value).toLowerCase();
    }
}
