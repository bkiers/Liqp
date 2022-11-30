package liqp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import liqp.blocks.Block;
import liqp.blocks.Capture;
import liqp.blocks.Case;
import liqp.blocks.Comment;
import liqp.blocks.Cycle;
import liqp.blocks.For;
import liqp.blocks.If;
import liqp.blocks.Ifchanged;
import liqp.blocks.Raw;
import liqp.blocks.Tablerow;
import liqp.blocks.Unless;
import liqp.filters.Filters;
import liqp.tags.Assign;
import liqp.tags.Break;
import liqp.tags.Continue;
import liqp.tags.Decrement;
import liqp.tags.Include;
import liqp.tags.Increment;

/**
 * An immutable map of {@link Insertion}s.
 * 
 * @author Christian Kohlschütter
 */
public final class Insertions {
    private final Map<String, Insertion> map;

    private final Set<String> blockNames;
    private final Set<String> tagNames;

    public static final Insertions EMPTY = new Insertions(Collections.emptyMap());

    /**
     * The standard insertions.
     */
    public static final Insertions STANDARD_INSERTIONS = Insertions.of( //
            new Assign(), //
            new Break(), //
            new Capture(), //
            new Case(), //
            new Comment(), //
            new Continue(), //
            new Cycle(), //
            new Decrement(), //
            new For(), //
            new If(), //
            new Ifchanged(), //
            new Include(), //
            new Increment(), //
            new Raw(), //
            new Tablerow(), //
            new Unless() //
    );

    /**
     * Creates a new {@link Insertions} instance with the given insertions.
     * 
     * @param insertions
     *            The insertions to add.
     */
    public static Insertions of(Collection<Insertion> insertions) {
        if (insertions.isEmpty()) {
            return EMPTY;
        }
        return new Insertions(insertions.stream().collect(Collectors.toMap(Insertion::getName, Function
                .identity())));
    }

    /**
     * Returns an {@link Insertions} instance with the given insertions.
     * 
     * @param insertions
     *            The insertions to add.
     */
    public static Insertions of(Insertion... insertions) {
        return of(Arrays.asList(insertions));
    }

    /**
     * Returns an {@link Insertions} instance with the given insertions.
     * 
     * @param insertions
     *            The insertions to add.
     */
    public static Insertions of(Map<String, Insertion> insertions) {
        if (insertions.isEmpty()) {
            return EMPTY;
        }

        return new Insertions(insertions);
    }

    private Insertions(Map<String, Insertion> insertions) {
        Objects.requireNonNull(insertions);

        this.map = new HashMap<>(insertions);

        this.blockNames = Collections.unmodifiableSet(getNames(en -> en.getValue() instanceof Block));
        this.tagNames = Collections.unmodifiableSet(getNames(en -> !(en.getValue() instanceof Block)));
    }

    void writeTo(Map<String, Insertion> target) {
        target.putAll(map);
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
    public Insertions mergeWith(Insertions other) {
        Objects.requireNonNull(other);

        if (other == this || other.map.isEmpty()) {
            return this;
        } else if (this.map.isEmpty()) {
            return other;
        }

        Map<String, Insertion> newMap = new HashMap<>(map);
        newMap.putAll(other.map);
        return new Insertions(newMap);
    }

    /**
     * Returns a set of names of insertions matching the given predicate.
     * 
     * @param predicate
     *            the predicate.
     * @return The set of names.
     */
    private Set<String> getNames(Predicate<? super Map.Entry<String, Insertion>> predicate) {
        return this.map.entrySet().stream().filter(predicate).map(x -> x.getKey()).collect(Collectors
                .toSet());
    }

    Set<String> getBlockNames() {
        return blockNames;
    }

    Set<String> getTagNames() {
        return tagNames;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Insertions)) {
            return false;
        }
        return ((Insertions) obj).map.equals(map);
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
     * Returns the {@link Insertion} registered under the given name, or {@code null} if no such
     * {@link Insertion} exists in this {@link Insertions} instance.
     * 
     * @param name
     *            The name of the {@link Insertion}.
     * @return The instance.
     */
    public Insertion get(String name) {
        return map.get(name);
    }
    
    /**
     * Returns an unmodifiable collection of the stored {@link Insertion}s.
     * 
     * @return The collection.
     */
    public Collection<Insertion> values() {
        return Collections.unmodifiableCollection(map.values());
    }
}
