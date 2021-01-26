package liqp.filters.date;

public interface CustomDateFormatSupport<T> {
    StrftimeCompatibleDate getValue(T value);

    boolean support(Object in);
}
