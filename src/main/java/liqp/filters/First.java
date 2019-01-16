package liqp.filters;

public class First extends Filter {

    /*
     * first(array)
     *
     * Get the first element of the passed in array
     */
    @Override
    public Object apply(Object value, Object... params) {

        Object[] array = super.asArray(value);

        return array.length == 0 ? null : array[0];
    }
}
