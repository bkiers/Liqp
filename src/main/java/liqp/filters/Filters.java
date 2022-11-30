package liqp.filters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An immutable map of {@link Filter}s.
 */
public final class Filters {
    private final Map<String, Filter> map;

    public static final Filters EMPTY = new Filters(Collections.emptyMap());

    static Filters COMMON_FILTERS = Filters.of( //
            new Abs(), //
            new Absolute_Url(), //
            new Append(), //
            new At_Least(), //
            new At_Most(), //
            new Capitalize(), //
            new Ceil(), //
            new Compact(), //
            new Concat(), //
            new Date(), //
            new Default(), //
            new Divided_By(), //
            new Downcase(), //
            new Escape(), //
            new Escape_Once(), //
            new First(), //
            new Floor(), //
            new H(), //
            new Join(), //
            new Last(), //
            new Lstrip(), //
            new liqp.filters.Map(), //
            new Minus(), //
            new Modulo(), //
            new Newline_To_Br(), //
            new Plus(), //
            new Prepend(), //
            new Remove(), //
            new Remove_First(), //
            new Replace(), //
            new Replace_First(), //
            new Reverse(), //
            new Round(), //
            new Rstrip(), //
            new Size(), //
            new Slice(), //
            new Sort(), //
            new Sort_Natural(), //
            new Split(), //
            new Strip(), //
            new Strip_HTML(), //
            new Strip_Newlines(), //
            new Times(), //
            new Truncate(), //
            new Truncatewords(), //
            new Uniq(), //
            new Upcase(), //
            new Url_Decode(), //
            new Url_Encode(), //
            new Where() //
    );

    static Filters JEKYLL_EXTRA_FILTERS = Filters.of( //
            new Normalize_Whitespace(), //
            new Push(), //
            new Relative_Url(), //
            new Where_Exp() //
    );

    public static Filters DEFAULT_FILTERS = COMMON_FILTERS;

    public static Filters JEKYLL_FILTERS = COMMON_FILTERS.mergeWith(JEKYLL_EXTRA_FILTERS);

    /**
     * Returns a {@link Filters} instance with the given filters.
     * 
     * @param filters
     *            The filters to add.
     */
    public static Filters of(Collection<Filter> filters) {
        if (filters.isEmpty()) {
            return EMPTY;
        }
        return new Filters(filters.stream().collect(Collectors.toMap(Filter::getName, Function
                .identity())));
    }

    /**
     * Returns a {@link Filters} instance with the given filters.
     * 
     * @param filters
     *            The filters to add.
     */
    public static Filters of(Map<String, Filter> filters) {
        if (filters.isEmpty()) {
            return EMPTY;
        }
        return new Filters(filters);
    }

    /**
     * Returns {@link Filters} instance with the given filters.
     * 
     * @param filters
     *            The filters to add.
     */
    public static Filters of(Filter... filters) {
        return of(Arrays.asList(filters));
    }

    private Filters(Map<String, Filter> filters) {
        Objects.requireNonNull(filters);

        this.map = Collections.unmodifiableMap(new HashMap<>(filters));
    }

    /**
     * Returns the filters as an unmodifiable {@link Map}. The keys are the {@link Filter#name}.
     * 
     * @return The map.
     */
    public Map<String, Filter> getMap() {
        return map;
    }

    /**
     * Returns a new {@link Filters} instance that combines this instance with the filters of the other
     * instance.
     * 
     * If there are filters with the same name in both instances, then the {@code other} filter takes
     * precedence.
     * 
     * @param other
     *            The other Filters instance.
     * @return A new, merged instance.
     */
    public Filters mergeWith(Filters other) {
        Objects.requireNonNull(other);

        if (other == this || other.map.isEmpty()) {
            return this;
        } else if (this.map.isEmpty()) {
            return other;
        }

        Map<String, Filter> newMap = new HashMap<>(map);
        newMap.putAll(other.map);
        return new Filters(newMap);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Filters)) {
            return false;
        }
        return ((Filters) obj).map.equals(map);
    }

    @Override
    public String toString() {
        if (map.isEmpty()) {
            return getClass().getName() + ".EMPTY";
        } else {
            return super.toString() + map;
        }
    }

    /**
     * Returns the {@link Filter} registered under the given name, or {@code null} if no such
     * {@link Filter} exists in this {@link Filters} instance.
     * 
     * @param name
     *            The name of the {@link Filter}.
     * @return The instance.
     */
    public Filter get(String name) {
        return map.get(name);
    }
    
    /**
     * Returns an unmodifiable collection of all Filters registered in this instance.
     * 
     * @return The collection.
     */
    public Collection<Filter> values() {
        return Collections.unmodifiableCollection(map.values());
    }
}
