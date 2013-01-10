package liqp.filters;

class first extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        Object[] array = super.asArray(value);

        return array.length == 0 ? "" : String.valueOf(array[0]);
    }
}
