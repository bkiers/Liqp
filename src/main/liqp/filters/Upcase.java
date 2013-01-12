package liqp.filters;

class Upcase extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        return super.asString(value).toUpperCase();
    }
}
