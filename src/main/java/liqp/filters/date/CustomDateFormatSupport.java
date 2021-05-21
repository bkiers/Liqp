package liqp.filters.date;

import java.time.ZonedDateTime;

public interface CustomDateFormatSupport<T> {
    ZonedDateTime getValue(T value);
    boolean support(Object in);
}
