package liqp.filters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Filter {

    private static final Map<String, Filter> FILTERS = new HashMap<String, Filter>();

    static {
        registerFilter(new append());
        registerFilter(new date());
        registerFilter(new capitalize());
        registerFilter(new first());
        registerFilter(new last());
        registerFilter(new downcase());
        registerFilter(new upcase());
        registerFilter(new replace());
        registerFilter(new replace_first());
        registerFilter(new remove_first());
        registerFilter(new remove());
        registerFilter(new times());
        registerFilter(new divided_by());
        registerFilter(new split());
        registerFilter(new modulo());
    }

    private static void registerFilter(Filter filter) {
        registerFilter(filter.getClass().getSimpleName(), filter);
    }

    public static void registerFilter(String id, Filter filter) {

        FILTERS.put(id, filter);
    }

    public Object[] asArray(Object value) {

        if(value.getClass().isArray()) {
            return (Object[])value;
        }

        if(value instanceof List) {
            return ((List)value).toArray();
        }

        return new String[]{ "" };
    }

    public String asString(Object value) {

        if(value instanceof Number) {

            double number = ((Number)value).doubleValue();

            if(number == (long)number) {
                return String.valueOf((long)number);
            }
        }

        return String.valueOf(value);
    }

    /*
        - date - reformat a date syntax reference
        - capitalize - capitalize words in the input sentence
        - downcase - convert an input string to lowercase
        - upcase - convert an input string to uppercase
        first - get the first element of the passed in array
        last - get the last element of the passed in array
        join - join elements of the array with certain character between them
        sort - sort elements of the array
        map - map/collect an array on a given property
        size - return the size of an array or string
        escape - escape a string
        escape_once - returns an escaped version of html without affecting existing escaped entities
        strip_html - strip html from string
        strip_newlines - strip all newlines (\n) from string
        newline_to_br - replace each newline (\n) with html break
        - replace - replace each occurrence e.g. {{ 'foofoo' | replace:'foo','bar' }} #=> 'barbar'
        - replace_first - replace the first occurrence e.g. {{ 'barbar' | replace_first:'bar','foo' }} #=> 'foobar'
        - remove - remove each occurrence e.g. {{ 'foobarfoobar' | remove:'foo' }} #=> 'barbar'
        - remove_first - remove the first occurrence e.g. {{ 'barbar' | remove_first:'bar' }} #=> 'bar'
        truncate - truncate a string down to x characters
        truncatewords - truncate a string down to x words
        prepend - prepend a string e.g. {{ 'bar' | prepend:'foo' }} #=> 'foobar'
        append - append a string e.g. {{ 'foo' | append:'bar' }} #=> 'foobar'
        minus - subtraction e.g. {{ 4 | minus:2 }} #=> 2
        plus - addition e.g. {{ '1' | plus:'1' }} #=> '11', {{ 1 | plus:1 }} #=> 2
        - times - multiplication e.g {{ 5 | times:4 }} #=> 20
        - divided_by - division e.g. {{ 10 | divided_by:2 }} #=> 5
        - split - split a string on a matching pattern e.g. {{ "a~b" | split:~ }} #=> ['a','b']
        - modulo - remainder, e.g. {{ 3 | modulo:2 }} #=> 1
    */

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
