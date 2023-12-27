package liqp.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.exceptions.VariableNotExistException;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

public class LookupNode implements LNode {
    private final String id;
    private final List<Indexable> indexes;

    public LookupNode(String id) {
        this.id = id;
        indexes = new ArrayList<>();
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

        AtomicBoolean found = new AtomicBoolean(false);
        if (context.containsKey(realId)) {
            value = context.get(realId);
            found.set(true);
        }
        if (value == null) {
            Map<String, Object> environmentMap = context.getEnvironmentMap();
            if (environmentMap.containsKey(realId)) {
                value = environmentMap.get(realId);
            }
        }

        boolean foundAllButLastOne = false;
        for (Iterator<Indexable> it = indexes.iterator(); it.hasNext();) {
            Indexable index = it.next();
            if (it.hasNext()) {
                value = index.get(value, context, found);
                if (value == null) {
                    found.set(false);
                    break;
                }
            } else {
                // last item
                value = index.get(value, context, found);
                if (!found.get()) {
                    foundAllButLastOne = true;
                }
            }
        }

        if (value == null && !found.get()) {
            final boolean error;
            switch (context.getParser().strictVariablesMode) {
                case OFF:
                    error = false;
                    break;
                case STRICT:
                    error = true;
                    break;
                case SANE:
                    error = !foundAllButLastOne;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

            if (error) {
                RuntimeException e = new VariableNotExistException(getVariableName());
                context.addError(e);
                if (context.getErrorMode() == TemplateParser.ErrorMode.STRICT) {
                    throw e;
                }
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
        Object get(Object value, TemplateContext context, AtomicBoolean found);
    }

    public static class Hash implements Indexable {

        private final String hash;

        public Hash(String hash) {
            this.hash = hash;
        }

        @Override
        public Object get(Object value, TemplateContext context, AtomicBoolean found) {
            found.set(true);
            if(value == null) {
                return null;
            }

            if(hash.equals("size")) {
                if(value instanceof Collection) {
                    return ((Collection<?>)value).size();
                }
                else if(value instanceof Map || value instanceof Inspectable) {
                    Map<?,?> map;
                    if (value instanceof Inspectable) {
                        LiquidSupport evaluated = context.getParser().evaluate(value);
                        map = evaluated.toLiquid();
                    } else {
                        map = (Map<?,?>) value;
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
                if(value instanceof List) {
                    List<?> list = (List<?>)value;
                    return list.isEmpty() ? null : list.get(0);
                }
                else if(value.getClass().isArray()) {
                    Object[] array = (Object[])value;
                    return array.length == 0 ? null : array[0];
                }
            }
            else if(hash.equals("last")) {
                if(value instanceof List) {
                    List<?> list = (List<?>)value;
                    return list.isEmpty() ? null : list.get(list.size() - 1);
                }
                else if(value.getClass().isArray()) {
                    Object[] array = (Object[])value;
                    return array.length == 0 ? null : array[array.length - 1];
                }
            }

            if(value instanceof Map || value instanceof Inspectable) {
                Map<?,?> map;
                if (value instanceof Inspectable) {
                    LiquidSupport evaluated = context.getParser().evaluate(value);
                    map = evaluated.toLiquid();
                } else {
                    map = (Map<?,?>) value;
                }
                if (!map.containsKey(hash)) {
                    found.set(false);
                    return null;
                }
                return map.get(hash);
            }
            else if(value instanceof TemplateContext) {
                return ((TemplateContext)value).get(hash, found);
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
        private final String text;
        private Object key = null;

        public Index(LNode expression, String text) {
            this.expression = expression;
            this.text = text;
        }

        @Override
        public Object get(Object value, TemplateContext context, AtomicBoolean found) {
            found.set(true);
            if(value == null) {
                return null;
            }

            key = expression.render(context);

            if (key instanceof Number) {
                int index = ((Number)key).intValue();

                if (value.getClass().isArray()) {
                    Object[] arr = ((Object[]) value);
                    int size = arr.length;
                    if (index >= size) {
                        return null;
                    } else if (index < 0) {
                        index = size + index;
                        if (index < 0) {
                            return null;
                        }
                    }
                    return arr[index];
                }
                else if (value instanceof List) {
                    List<?> list = ((List<?>) value);
                    int size = list.size();
                    if (index >= size) {
                        return null;
                    } else if (index < 0) {
                        index = size + index;
                        if (index < 0) {
                            return null;
                        }
                    }
                    return list.get(index);
                } else if (value instanceof Collection) {
                    Collection<?> coll = (Collection<?>) value;

                    int size = coll.size();
                    if (index >= size) {
                        return null;
                    } else if (index < 0) {
                        index = size + index;
                        if (index < 0) {
                            return null;
                        }
                    }

                    int i = 0;
                    for (Iterator<?> it = coll.iterator(); it.hasNext();) {
                        Object obj = it.next();
                        if (i == index) {
                            return obj;
                        }
                        i++;
                    }
                    return null;
                } else {
                    return null;
                }
            }
            else {

                // hashes only work on maps, not on arrays/lists
                if(value.getClass().isArray() || value instanceof List) {
                    return null;
                }

                String hash = String.valueOf(key);
                return new Hash(hash).get(value, context, found);
            }
        }

        @Override
        public String toString() {

            return String.format("[%s]", text);
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
