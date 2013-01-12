package liqp.filters;

class Downcase extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        return super.asString(value).toLowerCase();
    }
}
