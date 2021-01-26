package liqp.filters.date;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class StrftimeFormat extends Format {

    private SimpleDateFormat delegate;

    public StrftimeFormat() {

    }
    public StrftimeFormat(SimpleDateFormat delegate) {
        this.delegate = delegate;
    }

    @Override
    public StringBuffer format(Object inObj, StringBuffer toAppendTo, FieldPosition pos) {
        if (!(inObj instanceof StrftimeCompatibleDate)) {
            throw new IllegalArgumentException("object for formatting should be " + StrftimeCompatibleDate.class.getSimpleName()  + " type");
        }
        StrftimeCompatibleDate obj = (StrftimeCompatibleDate) inObj;
        if (delegate != null) {
            return delegate.format(obj.getDate(), toAppendTo, pos);
        }
        return null;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        if (delegate != null) {
            return delegate.parseObject(source, pos);
        }
        return null;
    }

}
