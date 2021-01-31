package liqp.filters.date;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.zone.ZoneRulesException;
import java.util.Locale;
import java.util.TimeZone;

public class StrftimeFormat extends Format {

    protected DateTimeFormatter delegate;

    public StrftimeFormat() {

    }
    public StrftimeFormat(DateTimeFormatter delegate) {
        this.delegate = delegate;
    }

    @Override
    public StringBuffer format(Object inObj, StringBuffer toAppendTo, FieldPosition pos) {
        ZonedDateTime obj = verifyType(inObj);
        if (obj == null) {
            return toAppendTo;
        }
        if (delegate != null) {
            String res = delegate.format(obj);
            return toAppendTo.append(res);
        }
        return null;
    }

    protected ZonedDateTime verifyType(Object inObj) {
        if (inObj == null) {
            return null;
        }
        if (!(inObj instanceof ZonedDateTime)) {
            throw new IllegalArgumentException("object for formatting should be " + StrftimeCompatibleDate.class.getSimpleName()  + " type");
        }
        return (ZonedDateTime) inObj;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        if (delegate != null) {
            return delegate.parse(source, pos);
        }
        return null;
    }

    /* package */ static class  TimeZoneHourOffsetStrftimeFormat extends StrftimeFormat {
        private final Locale locale;

        public TimeZoneHourOffsetStrftimeFormat(Locale locale) {
            this.locale = locale;
        }

        @Override
        public StringBuffer format(Object inObj, StringBuffer toAppendTo, FieldPosition pos) {
            ZonedDateTime obj = verifyType(inObj);
            if (obj == null) {
                return toAppendTo;
            }
            long offset = obj.get(ChronoField.OFFSET_SECONDS);
            boolean isPositive = offset >= 0;
            long hours = Math.abs(offset / (3600));
            long minutes = Math.abs(offset / (60) % (60));
            String strVal = String.format(locale, "%s%02d%02d", isPositive ? "+" : "-", hours, minutes);
            toAppendTo.append(strVal);
            return toAppendTo;
        }
    }

}
