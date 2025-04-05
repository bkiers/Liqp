package liqp.filters.date.fuzzy.extractors;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import liqp.filters.date.ArrayToRegexp;

abstract class EnumExtractor extends PartExtractorDelegate {

    protected ArrayToRegexp arrayToRegexp = new ArrayToRegexp();
    protected EnumExtractor(String name, Locale locale, String formatterPattern) {
        super(name);
        if (locale == null || Locale.ROOT.equals(locale)) {
            locale = Locale.US;
        }
        String valuesPattern = arrayToRegexp.apply(getEnumValues(locale), locale);
        super.delegates.add(new RegexPartExtractor(name,"(?:^|.*?[^\\w_])(" + valuesPattern + ")(?:$|[^\\w_].*?)",
                formatterPattern));
    }

    abstract String[] getEnumValues(Locale locale);

}
