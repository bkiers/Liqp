package liqp.filters.date.fuzzy.extractors;

import java.text.DateFormatSymbols;
import java.util.Locale;

class ShortMonthExtractor extends EnumExtractor {

    public ShortMonthExtractor(Locale locale) {
        super("ShortMonthExtractor", locale, "MMM");
    }

    @Override
    protected String[] getEnumValues(Locale locale) {
        return new DateFormatSymbols(locale).getShortMonths();
    }
}
