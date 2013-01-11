package liqp;

import java.util.List;

public abstract class LValue {

    public Object[] asArray(Object value) {

        if(value.getClass().isArray()) {
            return (Object[])value;
        }

        if(value instanceof List) {
            return ((List)value).toArray();
        }

        return new String[]{ "" };
    }

    public Number asNumber(Object value) {

        Number number = (Number)value;

        double d = number.doubleValue();

        if(d == (long)d) {
            return (long)d;
        }

        return d;
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
}
