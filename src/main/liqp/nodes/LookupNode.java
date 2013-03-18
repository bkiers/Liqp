package liqp.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class LookupNode implements LNode {

    private final String id;
    private final List<Indexable> indexes;

    public LookupNode(String id) {
        this.id = id;
        indexes = new ArrayList<Indexable>();
    }

    public void add(Indexable indexable) {
        indexes.add(indexable);
    }

    @Override
    public Object render(Map<String, Object> context) {

        Object value = context.get(id);

        /*
        Object value = context.get(ids.get(0));

        for (int i = 1; i < ids.size(); i++) {

            if (value == null) {
                return null;
            }

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                value = map.get(ids.get(i));
            }
            catch (Exception e) {
                return null;
            }
        }

        return value;
        */

        for(Indexable index : indexes) {

            value = index.get(value, context);
        }

        return value;
    }

    interface Indexable {
        Object get(Object value, Map<String, Object> context);
    }

    public static class Hash implements Indexable {

        private final String hash;

        public Hash(String hash) {
            this.hash = hash;
        }

        @Override
        public Object get(Object value, Map<String, Object> context) {

            if(value == null) {
                return null;
            }

            if(value instanceof java.util.Map) {
                return ((java.util.Map<?,?>)value).get(hash);
            }
            else {
                return null;
            }
        }
    }

    public static class Index implements Indexable {

        private final LNode expression;

        public Index(LNode expression) {
            this.expression = expression;
        }

        @Override
        public Object get(Object value, Map<String, Object> context) {

            if(value == null) {
                return null;
            }

            int index = ((Number)expression.render(context)).intValue();

            if(value.getClass().isArray()) {
                return ((Object[])value)[index];
            }
            else if(value instanceof List) {
                return ((List<?>)value).get(index);
            }
            else {
                return null;
            }
        }
    }
}
