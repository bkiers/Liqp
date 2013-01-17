package liqp.filters;

class H extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        // Alias for: escape
        return Filter.getFilter("escape").apply(value, params);
    }
}
