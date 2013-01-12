package liqp.filters;

class Last extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        Object[] array = super.asArray(value);

        return array.length == 0 ? "" : super.asString(array[array.length - 1]);
    }
}
