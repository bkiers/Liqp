package liqp.filters.date;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StrftimeFormat extends Format {

    protected SimpleDateFormat delegate;

    public StrftimeFormat() {

    }
    public StrftimeFormat(SimpleDateFormat delegate) {
        this.delegate = delegate;
    }

    @Override
    public StringBuffer format(Object inObj, StringBuffer toAppendTo, FieldPosition pos) {
        StrftimeCompatibleDate obj = verifyType(inObj);
        TimeZone targetTimeZone = TimeZone.getTimeZone(obj.getZoneId());
        // because of local timezome ALWAYS takes a part,
        // best way to get rid of it - get rid of it's offset
        long timezoneAlteredTime = obj.getDate() + targetTimeZone.getOffset(obj.getDate()) - TimeZone.getDefault().getOffset(obj.getDate());

        if (delegate != null) {
            return delegate.format(timezoneAlteredTime, toAppendTo, pos);
        }
        return null;
    }

    protected StrftimeCompatibleDate verifyType(Object inObj) {
        if (!(inObj instanceof StrftimeCompatibleDate)) {
            throw new IllegalArgumentException("object for formatting should be " + StrftimeCompatibleDate.class.getSimpleName()  + " type");
        }
        return (StrftimeCompatibleDate) inObj;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        if (delegate != null) {
            return delegate.parseObject(source, pos);
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
            StrftimeCompatibleDate obj = verifyType(inObj);
            String zoneId = obj.getZoneId();
            if (zoneId == null) {
                return toAppendTo;
            }
            TimeZone timeZone = TimeZone.getTimeZone(zoneId);
            // the amount of time in milliseconds to add to UTC to get local time.
            int offset = timeZone.getOffset(obj.getDate());
            boolean isPositive = offset >= 0;
            int hours = Math.abs(offset / (3600 * 1000));
            int minutes = Math.abs(offset / (60 * 1000) % (60));
            String strVal = String.format(locale, "%s%02d%02d", isPositive ? "+" : "-", hours, minutes);
            toAppendTo.append(strVal);
            return toAppendTo;
        }
    }

    /* package */ static class  TimeZoneNameStrftimeFormat extends StrftimeFormat {
        public TimeZoneNameStrftimeFormat(Locale locale) {
            this.delegate = new SimpleDateFormat("zzzz", locale);
        }

        @Override
        public StringBuffer format(Object inObj, StringBuffer toAppendTo, FieldPosition pos) {
            StrftimeCompatibleDate obj = verifyType(inObj);
            this.delegate.setTimeZone(TimeZone.getTimeZone(obj.getZoneId()));
            String val = delegate.format(obj.getDate());
            toAppendTo.append(val);
            return toAppendTo;
        }
    }

}
