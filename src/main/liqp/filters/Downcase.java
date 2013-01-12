package liqp.filters;

class Downcase extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        return String.valueOf(value).toLowerCase();
    }
}
