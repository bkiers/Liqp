package liqp.filters;

import liqp.LValue;
import liqp.TemplateContext;
import liqp.parser.Flavor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static liqp.ParseSettings.DEFAULT_FLAVOR;

/**
 * Output markup takes filters. Filters are simple methods. The first
 * parameter is always the output of the left side of the filter. The
 * return value of the filter will be the new left value when the next
 * filter is run. When there are no more filters, the template will
 * receive the resulting string.
 * <p/>
 * -- https://github.com/Shopify/liquid/wiki/Liquid-for-Designers
 */
public abstract class Filter extends LValue {

    /**
     * A map holding all common filters.
     */
    private static final Map<String, Filter> COMMON_FILTERS = new HashMap<>();

    private static final Map<String, Filter> JEKYLL_FILTERS = new HashMap<>();


    private static void addDefaultFilters() {
        registerFilter(new Abs());
        registerFilter(new Append());
        registerFilter(new At_Least());
        registerFilter(new At_Most());
        registerFilter(new Capitalize());
        registerFilter(new Ceil());
        registerFilter(new Compact());
        registerFilter(new Concat());
        registerFilter(new Date());
        registerFilter(new Default());
        registerFilter(new Divided_By());
        registerFilter(new Downcase());
        registerFilter(new Escape());
        registerFilter(new Escape_Once());
        registerFilter(new First());
        registerFilter(new Floor());
        registerFilter(new H());
        registerFilter(new Join());
        registerFilter(new Last());
        registerFilter(new Lstrip());
        registerFilter(new liqp.filters.Map());
        registerFilter(new Minus());
        registerFilter(new Modulo());
        registerFilter(new Newline_To_Br());
        registerFilter(new Plus());
        registerFilter(new Prepend());
        registerFilter(new Remove());
        registerFilter(new Remove_First());
        registerFilter(new Replace());
        registerFilter(new Replace_First());
        registerFilter(new Reverse());
        registerFilter(new Round());
        registerFilter(new Rstrip());
        registerFilter(new Size());
        registerFilter(new Slice());
        registerFilter(new Sort());
        registerFilter(new Sort_Natural());
        registerFilter(new Split());
        registerFilter(new Strip());
        registerFilter(new Strip_HTML());
        registerFilter(new Strip_Newlines());
        registerFilter(new Times());
        registerFilter(new Truncate());
        registerFilter(new Truncatewords());
        registerFilter(new Uniq());
        registerFilter(new Upcase());
        registerFilter(new Url_Decode());
        registerFilter(new Url_Encode());

        Filter filter = new Normalize_Whitespace();
        JEKYLL_FILTERS.put(filter.name, filter);

        registerFilter(new Where());
    }

    static {
        // Initialize all standard filters.
        addDefaultFilters();
    }

    /**
     * The name of the filter.
     */
    public final String name;

    /**
     * Used for all package protected filters in the liqp.filters-package
     * whose name is their class name lower cased.
     */
    protected Filter() {
        this.name = this.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Creates a new instance of a Filter.
     *
     * @param name
     *         the name of the filter.
     */
    public Filter(String name) {
        this.name = name;
    }

    /**
     * Applies the filter on the 'value'.
     *
     * @param value
     *         the string value `AAA` in: `{{ 'AAA' | f:1,2,3 }}`
     * @param params
     *         the values [1, 2, 3] in: `{{ 'AAA' | f:1,2,3 }}`
     *
     * @return the result of the filter.
     */
    public Object apply(Object value, Object... params) {

        // Default "no-op" filter.
        return value;
    }

    /**
     * Applies the filter on the 'value', with the given 'context'.
     *
     * @param value
     *         the string value `AAA` in: `{{ 'AAA' | f:1,2,3 }}`
     * @param context
     *         the template context.
     * @param params
     *         the values [1, 2, 3] in: `{{ 'AAA' | f:1,2,3 }}`
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
     *         the parameters to check.
     * @param expected
     *         the expected number of parameters.
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
                    (params == null ? 1 : (params.length + 1)) + " expected " + (min + 1) + ".." + (max + 1) + ")");
        }
    }

    /**
     * Returns a value at a specific index from an array of parameters.
     * If no such index exists, a RuntimeException is thrown.
     *
     * @param index
     *         the index of the value to be retrieved.
     * @param params
     *         the values.
     *
     * @return a value at a specific index from an array of
     *         parameters.
     */
    protected Object get(int index, Object... params) {

        if (index >= params.length) {
            throw new RuntimeException("error in filter '" + name +
                    "': cannot get param index: " + index +
                    " from: " + Arrays.toString(params));
        }

        return params[index];
    }

    /**
     * Retrieves a filter with a specific name.
     *
     * @param name
     *         the name of the filter to retrieve.
     *
     * @return a filter with a specific name.
     */
    public static Filter getFilter(String name) {

        Filter filter = COMMON_FILTERS.get(name);

        if (filter == null) {
            throw new RuntimeException("unknown filter: " + name);
        }

        return filter;
    }

    /**
     * Returns all default filters.
     *
     * @return all default filters.
     */
    public static Map<String, Filter> getFilters() {
        return getFilters(DEFAULT_FLAVOR);
    }

    public static Map<String, Filter> getFilters(Flavor flavor) {
        HashMap<String, Filter> filers = new HashMap<>(COMMON_FILTERS);
        if (Flavor.JEKYLL == flavor) {
            filers.putAll(JEKYLL_FILTERS);
        }
        return filers;
    }

    /**
     * Registers a new filter.
     *
     * @param filter
     *         the filter to be registered.
     */
    public static void registerFilter(Filter filter) {
        COMMON_FILTERS.put(filter.name, filter);
    }

    // for testing purpose
    private static void resetFilters() {
        COMMON_FILTERS.clear();
        JEKYLL_FILTERS.clear();
        addDefaultFilters();
    }
}
