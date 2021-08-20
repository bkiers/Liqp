package liqp.filters;

import liqp.TemplateContext;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

import java.util.*;

public class Sort extends Filter {

    /*
     * sort(input, property = nil)
     *
     * Sort elements of the array provide optional property with
     * which to sort an array of hashes or drops
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (value == null) {
            return "";
        }

        if(!super.isArray(value)) {
            throw new RuntimeException("cannot sort: " + value);
        }

        Object[] array = super.asArray(value, context);
        String property = params.length == 0 ? null : super.asString(params[0], context);

        List<Comparable> list = asComparableList(context, array, property);

        Collections.sort(list);

        return property == null ?
                list.toArray(new Comparable[list.size()]) :
                list.toArray(new SortableMap[list.size()]);
    }

    private List<Comparable> asComparableList(TemplateContext context, Object[] array, String property) {

        List<Comparable> list = new ArrayList<Comparable>();

        for (Object obj : array) {

            if (property != null && obj instanceof Inspectable) {
                LiquidSupport evaluated = context.renderSettings.evaluate(context.parseSettings.mapper, obj);
                obj = evaluated.toLiquid();
            }
            if(obj instanceof java.util.Map && property != null) {
                list.add(new SortableMap((java.util.Map<String, Comparable>)obj, property));
            }
            else {
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

            if(thisValue == null || thatValue == null) {
                throw new RuntimeException("Liquid error: comparison of Hash with Hash failed");
            }

            return thisValue.compareTo(thatValue);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            for(java.util.Map.Entry entry : super.entrySet()) {
                builder.append(entry.getKey()).append(entry.getValue());
            }

            return builder.toString();
        }
    }
}
