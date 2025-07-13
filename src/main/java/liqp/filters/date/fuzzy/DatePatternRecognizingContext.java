package liqp.filters.date.fuzzy;

import java.util.Locale;

public class DatePatternRecognizingContext {

    public final Locale locale;
    private final String input;
    public Boolean hasYear;
    public Boolean hasEra;
    public Boolean hasMonth;
    public Boolean hasDate;
    public Boolean weekDay;
    public Boolean hasTime;

    public DatePatternRecognizingContext(Locale locale, String input) {
        this.input = input;
        if (locale == null) {
            locale = Locale.ROOT;
        }
        this.locale = locale;
    }

}
