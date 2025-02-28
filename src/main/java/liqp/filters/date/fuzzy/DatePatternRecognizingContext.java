package liqp.filters.date.fuzzy;

import java.util.Locale;

public class DatePatternRecognizingContext {

    public final Locale locale;
    public Boolean hasYear;
    public Boolean hasEra;
    public Boolean hasMonth;
    public Boolean hasDate;
    public Boolean weekDay;
    public Boolean hasTime;

    public DatePatternRecognizingContext(Locale locale) {
        if (locale == null) {
            locale = Locale.ROOT;
        }
        this.locale = locale;
    }
}
