package liqp.filters;

class Last extends Filter {

    /*
     * last(array)
     *
     * Get the last element of the passed in array
     */
    @Override
    public Object apply(Object value, Object... params) {

        Object[] array = super.asArray(value);

        return array.length == 0 ? "" : super.asString(array[array.length - 1]);
    }
}
