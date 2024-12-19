package liqp.filters.date.fuzzy.extractors;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

abstract class EnumExtractor extends PartExtractorDelegate {

    public EnumExtractor(Locale locale, String formatterPattern) {
        if (locale == null || Locale.ROOT.equals(locale)) {
            locale = Locale.US;
        }
        String[] values = withoutNulls(getEnumValues(locale), locale);
        String valuesPattern = String.join("|", values);
        super.delegate = new RegexPartExtractor("(?:^|.*?[^\\w_])(" + valuesPattern + ")(?:$|[^\\w_].*?)",
                formatterPattern);
    }

    abstract protected String[] getEnumValues(Locale locale);

    protected String[] withoutNulls(String[] enumValues, Locale locale) {
        return Arrays.stream(enumValues)
                .filter(val -> val != null && !val.isEmpty())
                .map(s -> s.toLowerCase(locale))
                .map(Pattern::quote)
                .toArray(String[]::new);
    }
}
