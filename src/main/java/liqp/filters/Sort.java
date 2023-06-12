package liqp.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import liqp.TemplateContext;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

public class Sort extends Filter {

    /*
     * sort(input, property = nil)
     *
     * Sort elements of the array provide optional property with which to sort an array of hashes or
     * drops
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (value == null) {
            return "";
        }
        String property = params.length == 0 ? null : super.asString(params[0], context);

        Object[] array;
        boolean wasMap = false;
        if (value instanceof java.util.Map) {
            if (((java.util.Map<?, ?>) value).isEmpty()) {
                return value;
            }

            List<Object> list = new ArrayList<>();
            for (java.util.Map.Entry<Object, Object> en : ((java.util.Map<Object, Object>) value)
                .entrySet()) {
                list.add(new ComparableMapEntry<Object, Object>(en));
            }
            value = list;
            wasMap = true;
        } else if (value instanceof Collection) {
            if (((Collection<?>) value).isEmpty()) {
                return value;
            }
        }

        if (!super.isArray(value)) {
            throw new RuntimeException("cannot sort: " + value + "; type:" + (value == null ? "null"
                : value.getClass()));
        }

        array = super.asArray(value, context);

        List<Comparable> list = asComparableList(context, array, property);

        Collections.sort(list);

        if (wasMap) {
            LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
            for (ComparableMapEntry<Object, Object> en : (List<ComparableMapEntry<Object, Object>>) (List<?>) list) {
                map.put(en.getKey(), en.getValue());
            }
            return map;
        }

        return property == null ? list.toArray(new Comparable[list.size()]) : list.toArray(
            new SortableMap[list.size()]);
    }

    private static final class ComparableMapEntry<K, V> implements java.util.Map.Entry<K, V>,
        Comparable<java.util.Map.Entry<K, V>> {
        private final Entry<K, V> entry;

        ComparableMapEntry(java.util.Map.Entry<K, V> entry) {
            this.entry = entry;
        }

        @Override
        public K getKey() {
            return entry.getKey();
        }

        @Override
        public V getValue() {
            return entry.getValue();
        }

        @Override
        public V setValue(V value) {
            return entry.setValue(value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compareTo(Entry<K, V> o) {
            return ((Comparable<Object>) getKey()).compareTo(o.getKey());
        }

        @Override
        public String toString() {
            return entry.toString();
        }
    }

    private List<Comparable> asComparableList(TemplateContext context, Object[] array, String property) {

        List<Comparable> list = new ArrayList<Comparable>();

        for (Object obj : array) {

            if (property != null && obj instanceof Inspectable) {
                LiquidSupport evaluated = context.getParser().evaluate(
                        obj);
                obj = evaluated.toLiquid();
            }
            if (obj instanceof java.util.Map && property != null) {
                list.add(new SortableMap((java.util.Map<String, Comparable>) obj, property));
            } else {
                list.add((Comparable) obj);
            }
        }

        return list;
    }

    static class SortableMap extends HashMap<String, Comparable> implements Comparable<SortableMap> {

        final String property;

        SortableMap(java.util.Map<String, Comparable> map, String property) {
            super.putAll(map);
            this.property = property;
        }

        @Override
        public int compareTo(SortableMap that) {

            Comparable thisValue = this.get(property);
            Comparable thatValue = that.get(property);

            if (thisValue == null || thatValue == null) {
                throw new RuntimeException("Liquid error: comparison of Hash with Hash failed");
            }

            return thisValue.compareTo(thatValue);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            for (java.util.Map.Entry entry : super.entrySet()) {
                builder.append(entry.getKey()).append(entry.getValue());
            }

            return builder.toString();
        }
    }
}
