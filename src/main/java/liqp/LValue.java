package liqp;

import static liqp.filters.date.Parser.getZonedDateTimeFromTemporalAccessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import liqp.RenderTransformer.ObjectAppender;
import liqp.filters.date.CustomDateFormatRegistry;
import liqp.nodes.AtomNode;

/**
 * An abstract class the Filter and Tag classes extend.
 * <p/>
 * It houses some utility methods easily available for said
 * classes.
 */
public abstract class LValue {

    public static final LValue BREAK = new LValue() {
        @Override
        public String toString() {
            return "";
        }
    };

    public static final LValue CONTINUE = new LValue() {
        @Override
        public String toString() {
            return "";
        }
    };

    // sample: 2007-11-01 15:25:00 +0900
    public static DateTimeFormatter rubyDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XX");

    /**
     * Returns true iff a and b are equals, where (int) 1 is
     * equal to (double) 1.0
     *
     * @param a
     *         the first object to compare.
     * @param b
     *         the second object to compare.
     *
     * @return true iff a and b are equals, where (int) 1 is
     *         equals to (double) 1.0
     */
    public static boolean areEqual(Object a, Object b) {

        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        // TODO refactor the instance-ofs below

        if (a instanceof Number && b instanceof Number) {

            double delta = ((Number) a).doubleValue() - ((Number) b).doubleValue();

            // To account for floating point rounding errors, return true if
            // the difference between double a and double b is very small.
            return Math.abs(delta) < 0.00000000001;
        }

        if (isEmpty(a) && isEmpty(b)) {
            return true;
        }

        return a.equals(b);
    }


    private static boolean isEmpty(Object val) {
        if (AtomNode.isEmpty(val)) {
            return true;
        }
        if ((val instanceof CharSequence)) {
            return ((CharSequence)val).length() == 0;
        }
        if (val instanceof Collection) {
            return ((Collection<?>) val).isEmpty();
        }
        if (val instanceof Map) {
            return ((Map<?, ?>) val).isEmpty();
        }
        if (val.getClass().isArray()) {
            return ((Object[])val).length == 0;
        }
        return false;
    }

    /**
     * Returns this value as an array. If a value is already an array,
     * it is cast to a `Object[]`, if it's a `java.util.Collection`, it is
     * converted to an array and in all other cases, `value` is simply
     * returned as an `Object[]` with a single value in it.
     * This function treat `Map` as single element.
     *
     * @param value
     *         the value to convert/cast to an array.
     *
     * @return this value as an array.
     */
    public Object[] asArray(Object value, TemplateContext context) {

        if(value == null) {
            return new Object[]{};
        }

        if (value.getClass().isArray()) {
            return (Object[]) value;
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).toArray();
        }

        if (isTemporal(value)) {
            value = asTemporal(value, context);
            return temporalAsArray((ZonedDateTime) value);
        }

        return new Object[]{value};
    }

    /**
     * Returns this value as a `java.util.List`. If a value is already a `List`,
     * it is returned as-is, if it's a `java.util.Collection` or an array, it is
     * converted to a `List`, and in all other cases, `value` is simply
     * returned as a `List` with a single value in it.
     * This function treats `Map` as a single element.
     *
     * @param value
     *         the value to convert/cast to an array.
     *
     * @return this value as a list.
     */
    public List<?> asList(Object value, TemplateContext context) {
        if (value == null) {
            return Collections.emptyList();
        }

        if (value instanceof List) {
            return (List<?>) value;
        }

        if (value instanceof Collection) {
            return new ArrayList<>(((Collection<?>) value));
        }

        if (value.getClass().isArray()) {
            return Arrays.asList((Object[]) value);
        }

        if (isTemporal(value)) {
            value = asTemporal(value, context);
            return Arrays.asList(temporalAsArray((ZonedDateTime) value));
        }

        return Collections.singletonList(value);
    }

    // https://apidock.com/ruby/Time/to_a
    // Returns a ten-element array of values for time:
    // [sec, min, hour, day, month, year, wday, yday, isdst, zone]
    // t = Time.now     #=> 2007-11-19 08:36:01 -0600
    // now = t.to_a     #=> [1, 36, 8, 19, 11, 2007, 1, 323, false, "CST"]
    public static Object[] temporalAsArray(ZonedDateTime time) {
        int sec = time.get(ChronoField.SECOND_OF_MINUTE);
        int min = time.getMinute();
        int hour = time.getHour();
        int day = time.getDayOfMonth();
        int month = time.get(ChronoField.MONTH_OF_YEAR);
        int year = time.get(ChronoField.YEAR);
        int wday = time.getDayOfWeek().getValue();
        int yday = time.get(ChronoField.DAY_OF_YEAR);
        boolean isdst = time.getZone().getRules().isDaylightSavings(time.toInstant());
        String zone = time.getZone().getId();
        return new Object[]{sec, min, hour, day, month, year, wday, yday, isdst, zone};
    }

    /**
     * This one keeps an original temporal type as is, while `asRubyDate` converts it to ZonedDateTime.
     */
    public static TemporalAccessor asTemporal(Object value, TemplateContext context) {
        ZonedDateTime time = ZonedDateTime.now();
        if (value instanceof TemporalAccessor) {
            return (TemporalAccessor) value;
        } else if (CustomDateFormatRegistry.isCustomDateType(value)) {
            time = CustomDateFormatRegistry.getFromCustomType(value);
        }
        return time;
    }

    /**
     * Ruby have a single date type, and its equivalent is ZonedDateTime.
     */
    public static ZonedDateTime asRubyDate(Object value, TemplateContext context) {
        ZonedDateTime time = ZonedDateTime.now();
        if (value instanceof TemporalAccessor) {
            time = getZonedDateTimeFromTemporalAccessor((TemporalAccessor) value, context.getParser().defaultTimeZone);
        } else if (CustomDateFormatRegistry.isCustomDateType(value)) {
            time = CustomDateFormatRegistry.getFromCustomType(value);
        }
        return time;
    }

    public static boolean isTemporal(Object value){
        boolean isTemporalAccessor = value instanceof TemporalAccessor;
        boolean isCustomDateType = CustomDateFormatRegistry.isCustomDateType(value);
        return isTemporalAccessor || isCustomDateType;
    }

    /**
     * Usually we need array representation of items, so the {@link #asArray(Object, TemplateContext)} do the work well.
     * But occasionally we need introspect the object (usually `Map`) as array.
     * So this function do so.
     *
     * @param value
     * @return
     */
    protected Object[] mapAsArray(Map<?,?> value) {
        List<Object[]> keyValuePairs = new ArrayList<>();
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            keyValuePairs.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        return keyValuePairs.toArray();
    }

    /**
     * Convert `value` to a boolean. Note that only `nil` and `false`
     * are `false`, all other values are `true`.
     *
     * @param value
     *         the value to convert.
     *
     * @return `value` as a boolean.
     */
    public boolean asBoolean(Object value) {

        if (value == null) {
            return false;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return true;
    }

    /**
     * Returns `value` as a Number. Strings will be coerced into
     * either a Long or Double.
     *
     * @param value
     *         the value to cast to a Number.
     *
     * @return `value` as a Number.
     *
     * @throws NumberFormatException when `value` is a String which could
     *                               not be parsed as a Long or Double.
     */
    public Number asNumber(Object value) throws NumberFormatException {

        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            return (Number) value;
        }

        String str = String.valueOf(value).trim();

        return str.matches("\\d+") ? Long.valueOf(str) : Double.valueOf(str);
    }

    public BigDecimal asStrictNumber(Object number) {
        if (number == null) {
            return null;
        }
        if (number instanceof PlainBigDecimal) {
            return (PlainBigDecimal) number;
        }
        return new PlainBigDecimal(number.toString().trim());
    }

    // mimic ruby's `BigDecimal.to_f` with standard java capabilities
    // same time provide expected out for java.math.BigDecimal
    public static BigDecimal asFormattedNumber(BigDecimal bd) {
        return bd.setScale(Math.max(1, bd.stripTrailingZeros().scale()), RoundingMode.UNNECESSARY);
    }
    
    /**
     * Returns `value` as a String.
     *
     * @param value
     *         the value to convert to a String.
     *
     * @return `value` as a String.
     */
    public String asString(Object value, TemplateContext context) {

        if (value == null) {
            return "";
        }

        if (isTemporal(value)) {
            ZonedDateTime time = asRubyDate(value, context);
            return rubyDateTimeFormat.format(time);
        }

        if (!isArray(value)) {
            return String.valueOf(value);
        }

        Object[] array = this.asArray(value, context);

        StringBuilder builder = new StringBuilder();

        for (Object obj : array) {
            builder.append(this.asString(obj, context));
        }

        return builder.toString();
    }
    
    /**
     * Returns `value` as an object appendable to {@link ObjectAppender}.
     *
     * @param value
     *         the value to convert.
     *
     * @return `value` as an appendable object.
     */
    public Object asAppendableObject(Object value, TemplateContext context) {
        if (value == null) {
            return "";
        }

        if (isTemporal(value)) {
            ZonedDateTime time = asRubyDate(value, context);
            return rubyDateTimeFormat.format(time);
        }

        if (!isArray(value)) {
            return value;
        }

        Object[] array = this.asArray(value, context);

        ObjectAppender.Controller builder = context.newObjectAppender(array.length);

        for (Object obj : array) {
            builder.append(this.asAppendableObject(obj, context));
        }

        return builder.getResult();
    }

    /**
     * Returns true iff `value` is an array or a java.util.Collection.
     *
     * @param value
     *         the value to check.
     *
     * @return true iff `value` is an array or a java.util.Collection.
     */
    public boolean isArray(Object value) {

        return value != null && (value.getClass().isArray() || value instanceof Collection);
    }

    /**
     * Returns true iff `value` is a whole number (Integer or Long).
     *
     * @param value
     *         the value to check.
     *
     * @return true iff `value` is a whole number (Integer or Long).
     */
    public boolean isInteger(Object value) {
        return (value instanceof Long || value instanceof Integer);
    }

    /**
     * Returns true iff `value` is a Number.
     *
     * @param value
     *         the value to check.
     *
     * @return true iff `value` is a Number.
     */
    public boolean isNumber(Object value) {

        if(value == null) {
            return false;
        }

        if(value instanceof Number) {
            return true;
        }

        // valid Long?
        if(String.valueOf(value).trim().matches("\\d+")) {
            return true;
        }

        try {
            // valid Double?
            Double.parseDouble(String.valueOf(value).trim());
        } catch(Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Returns true iff `value` is a String.
     *
     * @param value
     *         the value to check.
     *
     * @return true iff `value` is a String.
     */
    public boolean isString(Object value) {

        return value != null && value instanceof CharSequence;
    }

    public boolean isTruthy(Object value, TemplateContext context) {
        return !this.isFalsy(value, context);
    }

    public boolean isFalsy(Object value, TemplateContext context) {

        if (value == null)
            return true;

        if (value instanceof Boolean && !((Boolean) value))
            return true;

        if (value instanceof CharSequence && ((CharSequence) value).length() == 0)
            return true;

        if (this.isArray(value) && this.asArray(value, context).length == 0)
            return true;

        if ((value instanceof Map) && ((Map<?,?>) value).isEmpty())
            return true;

        return false;
    }

    public boolean canBeInteger(Object value) {
        return String.valueOf(value).trim().matches("-?\\d+");
    }

    public boolean canBeDouble(Object value) {
        return String.valueOf(value).trim().matches("-?\\d+(\\.\\d*)?");
    }

    public boolean canBeNumber(Object value) {
        if (value == null) {
            return false;
        }
        return canBeInteger(value) || canBeDouble(value);
    }

    public boolean isMap(Object value) {
        return (value instanceof Map);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> asMap(Object value) {
        return (Map<String, Object>)value;
    }
}
