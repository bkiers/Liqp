package liqp.filters.date.fuzzy.extractors;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import liqp.filters.date.fuzzy.Part;
import liqp.filters.date.fuzzy.PartExtractor;

public class MonthNameExtractor extends PartExtractor {
    private final List<EnumExtractor> monthExtractors;

    public MonthNameExtractor(Locale locale) {
        super("MonthNameExtractor");
        this.monthExtractors = new ArrayList<>();
        this.monthExtractors.add(new EnumExtractor("FullMonthExtractor", locale, "MMMM") {
            @Override
            protected String[] getEnumValues(Locale locale) {
                return new DateFormatSymbols(locale).getMonths();
            }
        });
        this.monthExtractors.add(new EnumExtractor("ShortMonthExtractor", locale, "MMM") {
            @Override
            protected String[] getEnumValues(Locale locale) {
                return new DateFormatSymbols(locale).getShortMonths();
            }
        });

    }
    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        PartExtractorResult res  = new PartExtractorResult(this.name);
        for (EnumExtractor monthExtractor : monthExtractors) {
            PartExtractorResult monthResult = monthExtractor.extract(source, parts, i);
            if (monthResult.found) {
                monthResult.isMonthName = true;
                return monthResult;
            }
        }
        return res;
    }
}
