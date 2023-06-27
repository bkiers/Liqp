package liqp.filters;

import java.util.Arrays;
import java.util.Locale;

import liqp.LValue;
import liqp.TemplateContext;

/**
 * Output markup takes filters. Filters are simple methods. The first parameter is always the output of
 * the left side of the filter. The return value of the filter will be the new left value when the next
 * filter is run. When there are no more filters, the template will receive the resulting string.
 * <p/>
 * -- https://github.com/Shopify/liquid/wiki/Liquid-for-Designers
 */
public abstract class Filter extends LValue {

    /**
     * The name of the filter.
     */
    public final String name;

    /**
     * Used for all package protected filters in the liqp.filters-package whose name is their class name
     * lower cased.
     */
    protected Filter() {
        this.name = this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Creates a new instance of a Filter.
     *
     * @param name
     *            the name of the filter.
     */
    public Filter(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the filter.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Applies the filter on the 'value'.
     *
     * @param value
     *            the string value `AAA` in: `{{ 'AAA' | f:1,2,3 }}`
     * @param params
     *            the values [1, 2, 3] in: `{{ 'AAA' | f:1,2,3 }}`
     *
     * @deprecated use {@link #apply(Object, TemplateContext, Object...)}
     * @return the result of the filter.
     */
    @Deprecated
    public Object apply(Object value, Object... params) {

        // Default "no-op" filter.
        return value;
    }

    /**
     * Applies the filter on the 'value', with the given 'context'.
     *
     * @param value
     *            the string value `AAA` in: `{{ 'AAA' | f:1,2,3 }}`
     * @param context
     *            the template context.
     * @param params
     *            the values [1, 2, 3] in: `{{ 'AAA' | f:1,2,3 }}`
     *
     * @return the result of the filter.
     */
    public Object apply(Object value, TemplateContext context, Object... params) {

        return apply(value, params);
    }

    /**
     * Check the number of parameters and throws an exception if needed.
     *
     * @param params
     *            the parameters to check.
     * @param expected
     *            the expected number of parameters.
     */
    final void checkParams(Object[] params, int expected) {

        if (params == null || params.length != expected) {
            throw new RuntimeException("Liquid error: wrong number of arguments (given " +
                    (params == null ? 1 : (params.length + 1)) + " for " + (expected + 1) + ")");
        }
    }

    final void checkParams(Object[] params, int min, int max) {

        if (params == null || params.length < min || params.length > max) {
            throw new RuntimeException("Liquid error: wrong number of arguments (given " +
                    (params == null ? 1 : (params.length + 1)) + " expected " + (min + 1) + ".." + (max
                            + 1) + ")");
        }
    }

    /**
     * Returns a value at a specific index from an array of parameters. If no such index exists, a
     * RuntimeException is thrown.
     *
     * @param index
     *            the index of the value to be retrieved.
     * @param params
     *            the values.
     *
     * @return a value at a specific index from an array of parameters.
     */
    protected Object get(int index, Object... params) {

        if (index >= params.length) {
            throw new RuntimeException("error in filter '" + name + "': cannot get param index: " +
                    index + " from: " + Arrays.toString(params));
        }

        return params[index];
    }

}
