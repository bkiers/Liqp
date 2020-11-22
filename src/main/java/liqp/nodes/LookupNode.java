package liqp.nodes;

import liqp.TemplateContext;
import liqp.exceptions.VariableNotExistException;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LookupNode implements LNode {

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
    public Object render(TemplateContext context) {

        Object value = null;

        String realId;
        // Check if there's a [var] lookup, AST: ^(LOOKUP Id["@var"])
        if(id.startsWith("@")) {
            realId = String.valueOf(context.get(id.substring(1)));
        } else {
            realId = id;
        }
        if (context.containsKey(realId)) {
            value = context.get(realId);
        }
        if (value == null) {
            Map<String, Object> environmentMap = context.getEnvironmentMap();
            if (environmentMap.containsKey(realId)) {
                value = environmentMap.get(realId);
            }
        }

        for(Indexable index : indexes) {
            value = index.get(value, context);
        }

        if(value == null && context.renderSettings.strictVariables) {
            RuntimeException e = new VariableNotExistException(getVariableName());
            context.addError(e);

            if (context.renderSettings.raiseExceptionsInStrictMode) {
                throw e;
            }
        }

        return value;
    }

    private String getVariableName() {
        StringBuilder variableFullName = new StringBuilder(id);
        for(Indexable index : indexes) {
            variableFullName.append(index.toString());
        }
        return variableFullName.toString();
    }

    public interface Indexable {
        Object get(Object value, TemplateContext context);
    }

    public static class Hash implements Indexable {

        private final String hash;

        public Hash(String hash) {
            this.hash = hash;
        }

        @Override
        public Object get(Object value, TemplateContext context) {

            if(value == null) {
                return null;
            }

            if(hash.equals("size")) {
                if(value instanceof Collection) {
                    return ((Collection)value).size();
                }
                else if(value instanceof java.util.Map || value instanceof Inspectable) {
                    java.util.Map map;
                    if (value instanceof Inspectable) {
                        LiquidSupport evaluated = context.renderSettings.evaluate(context.parseSettings.mapper, (Inspectable) value);
                        map = evaluated.toLiquid();
                    } else {
                        map = (java.util.Map) value;
                    }
                    return map.containsKey(hash) ? map.get(hash) : map.size();
                }
                else if(value.getClass().isArray()) {
                    return ((Object[])value).length;
                }
                else if(value instanceof CharSequence) {
                    CharSequence charSequence = (CharSequence)value;
                    return charSequence.length();
                }
            }
            else if(hash.equals("first")) {
                if(value instanceof java.util.List) {
                    java.util.List list = (java.util.List)value;
                    return list.isEmpty() ? null : list.get(0);
                }
                else if(value.getClass().isArray()) {
                    Object[] array = (Object[])value;
                    return array.length == 0 ? null : array[0];
                }
            }
            else if(hash.equals("last")) {
                if(value instanceof java.util.List) {
                    java.util.List list = (java.util.List)value;
                    return list.isEmpty() ? null : list.get(list.size() - 1);
                }
                else if(value.getClass().isArray()) {
                    Object[] array = (Object[])value;
                    return array.length == 0 ? null : array[array.length - 1];
                }
            }

            if(value instanceof java.util.Map || value instanceof Inspectable) {
                java.util.Map map;
                if (value instanceof Inspectable) {
                    LiquidSupport evaluated = context.renderSettings.evaluate(context.parseSettings.mapper, (Inspectable) value);
                    map = evaluated.toLiquid();
                } else {
                    map = (java.util.Map) value;
                }
                return map.get(hash);
            }
            else if(value instanceof TemplateContext) {
                return ((TemplateContext)value).get(hash);
            } else {
                return null;
            }
        }

        @Override
        public String toString() {

            return String.format(".%s", hash);
        }
    }

    public static class Index implements Indexable {

        private final LNode expression;
        private Object key = null;

        public Index(LNode expression) {
            this.expression = expression;
        }

        @Override
        public Object get(Object value, TemplateContext context) {

            if(value == null) {
                return null;
            }

            key = expression.render(context);

            if(key instanceof Number) {
                int index = ((Number)key).intValue();

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
            else {

                // hashes only work on maps, not on arrays/lists
                if(value.getClass().isArray() || value instanceof List) {
                    return null;
                }

                String hash = String.valueOf(key);
                return new Hash(hash).get(value, context);
            }
        }

        @Override
        public String toString() {

            return String.format("[%s]", key);
        }
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(id);

        for(Indexable index : this.indexes) {
            builder.append(index.toString());
        }

        return builder.toString();
    }
}
