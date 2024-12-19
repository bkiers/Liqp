package liqp.filters.date.fuzzy;

import java.util.Locale;

public class DatePatternRecognizingContext {

    final Locale locale;
    Boolean hasYear;
    Boolean hasMonth;
    Boolean hasDay;
    Boolean weekDay;
    Boolean hasTime;

    public DatePatternRecognizingContext(Locale locale) {
        if (locale == null) {
            locale = Locale.ROOT;
        }
        this.locale = locale;
    }
}
