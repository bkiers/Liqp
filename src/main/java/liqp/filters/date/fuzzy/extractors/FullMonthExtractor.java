package liqp.filters.date.fuzzy.extractors;

import java.text.DateFormatSymbols;
import java.util.Locale;

class FullMonthExtractor extends EnumExtractor {

    public FullMonthExtractor(Locale locale) {
        super("FullMonthExtractor", locale, "MMMM");
    }

    @Override
    protected String[] getEnumValues(Locale locale) {
        return new DateFormatSymbols(locale).getMonths();
    }
}
