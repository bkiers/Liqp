package liqp;

import java.util.List;

public abstract class LValue {

    public static boolean areEqual(Object a, Object b) {

        if(a == b) {
            return true;
        }

        if(a == null || b == null) {
            return false;
        }

        if(a instanceof Number && b instanceof Number) {
            return ((Number)a).doubleValue() == ((Number)b).doubleValue();
        }

        return a.equals(b);
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

    public boolean asBoolean(Object value) {

        if(value == null) {
            return false;
        }

        if(value instanceof Boolean) {
            return (Boolean)value;
        }

        return true;
    }

    public Number asNumber(Object value) {

        return (Number) value;
    }

    public String asString(Object value) {

        if(value == null) {
            return "";
        }

        if(!this.isArray(value)) {
            return String.valueOf(value);
        }

        Object[] array = this.asArray(value);

        StringBuilder builder = new StringBuilder();

        for(Object obj : array) {
            builder.append(this.asString(obj));
        }

        return builder.toString();
    }

    public boolean isArray(Object value) {

        return value != null && (value.getClass().isArray() || value instanceof List);
    }

    public boolean isNumber(Object value) {

        return value != null && value instanceof Number;
    }

    public boolean isString(Object value) {

        return value != null && value instanceof CharSequence;
    }
}
