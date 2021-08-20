package liqp.filters;

import liqp.TemplateContext;

public class Last extends Filter {

    /*
     * last(array)
     *
     * Get the last element of the passed in array
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        Object[] array = super.asArray(value, context);

        return array.length == 0 ? null : array[array.length - 1];
    }
}
