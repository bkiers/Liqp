package liqp.filters;

import static liqp.ParseSettings.DEFAULT_FLAVOR;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import liqp.LValue;
import liqp.ParseSettings;
import liqp.TemplateContext;
import liqp.parser.Flavor;

/**
 * Output markup takes filters. Filters are simple methods. The first parameter is always the output of
 * the left side of the filter. The return value of the filter will be the new left value when the next
 * filter is run. When there are no more filters, the template will receive the resulting string.
 * <p/>
 * -- https://github.com/Shopify/liquid/wiki/Liquid-for-Designers
 */
public abstract class Filter extends LValue {

    /**
     * A map holding all common filters.
     */
    private static final Map<String, Filter> COMMON_FILTERS = new HashMap<>();

    private static final Map<String, Filter> JEKYLL_FILTERS = new HashMap<>();

    private static Filters CURRENT_COMMON_FILTERS = null;
    private static Filters CURRENT_JEKYLL_FILTERS = null;

    private static void addDefaultFilters() {
        COMMON_FILTERS.putAll(Filters.COMMON_FILTERS.getMap());
        JEKYLL_FILTERS.putAll(Filters.JEKYLL_EXTRA_FILTERS.getMap());
        updateCommonFilters();
    }

    private static void updateCommonFilters() {
        CURRENT_COMMON_FILTERS = Filters.of(COMMON_FILTERS);
        CURRENT_JEKYLL_FILTERS = CURRENT_COMMON_FILTERS.mergeWith(Filters.of(JEKYLL_FILTERS));
    }

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

    /**
     * Retrieves a filter with a specific name.
     *
     * @param name
     *            the name of the filter to retrieve.
     *
     * @return a filter with a specific name.
     * @deprecated Use {@link Filters#get(String)}
     */
    @Deprecated
    public static Filter getFilter(String name) {
        checkInitialized();

        Filter filter = COMMON_FILTERS.get(name);

        if (filter == null) {
            throw new RuntimeException("unknown filter: " + name);
        }

        return filter;
    }

    /**
     * Registers a new filter.
     * 
     * If a filter exists under the same name, it is replaced by this one (except for three special
     * Jekyll filters).
     * 
     * Note that this method is unsafe, as it affects all uses of this class.
     * 
     * @param filter
     *            the filter to be registered.
     * @deprecated Use {@link liqp.ParseSettings.Builder#with(Filter)}
     */
    @Deprecated
    public static void registerFilter(Filter filter) {
        checkInitialized();
        COMMON_FILTERS.put(filter.name, filter);
        updateCommonFilters();
    }

    private static void checkInitialized() {
        if (CURRENT_COMMON_FILTERS == null) {
            resetFilters();
        }
    }

    private static void resetFilters() {
        COMMON_FILTERS.clear();
        JEKYLL_FILTERS.clear();
        addDefaultFilters();
        updateCommonFilters();
    }
}
