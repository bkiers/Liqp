package liqp.filters;

import liqp.LValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Filter extends LValue {

    private static final Map<String, Filter> FILTERS = new HashMap<String, Filter>();

    static {
        registerFilter(new Append());
        registerFilter(new Date());
        registerFilter(new Capitalize());
        registerFilter(new First());
        registerFilter(new Join());
        registerFilter(new Last());
        registerFilter(new liqp.filters.Map());
        registerFilter(new Downcase());
        registerFilter(new Upcase());
        registerFilter(new Replace());
        registerFilter(new Replace_First());
        registerFilter(new Remove_First());
        registerFilter(new Remove());
        registerFilter(new Times());
        registerFilter(new Divided_By());
        registerFilter(new Sort());
        registerFilter(new Split());
        registerFilter(new Modulo());
    }

    private static void registerFilter(Filter filter) {
        registerFilter(filter.getClass().getSimpleName().toLowerCase(), filter);
    }

    public static void registerFilter(String id, Filter filter) {

        FILTERS.put(id, filter);
    }

    public final String name;

    protected Filter() {
        this.name = this.getClass().getSimpleName();
    }

    public Filter(String name) {
        this.name = name;
    }

    public abstract Object apply(Object value, Object... params);

    protected Object get(int index, Object... params) {

        if(index >= params.length) {
            throw new RuntimeException("error in filter '" + name +
                    "': cannot get param index: " + index +
                    " from: " + Arrays.toString(params));
        }

        return params[index];
    }

    public static Filter getFilter(String name) {

        Filter filter = FILTERS.get(name);

        if(filter == null) {
            throw new RuntimeException("unknown filter: " + name);
        }

        return filter;
    }
}
