package liqp.filters;

class last extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        Object[] array = super.asArray(value);

        return array.length == 0 ? "" : String.valueOf(array[array.length - 1]);
    }
}
