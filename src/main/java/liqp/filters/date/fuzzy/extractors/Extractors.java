package liqp.filters.date.fuzzy.extractors;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import liqp.filters.date.fuzzy.PartExtractor;

public enum Extractors {
    fullWeekdaysExtractor {
        private final Map<Locale, PartExtractor> extractors = new HashMap<>();
        @Override
        public PartExtractor get(Locale locale) {
            return extractors.computeIfAbsent(locale, l -> new EnumExtractor("fullWeekdaysExtractor", locale, "EEEE") {
                @Override
                protected String[] getEnumValues(Locale locale) {
                    return new DateFormatSymbols(locale).getWeekdays();
                }

                @Override
                protected void visitPER(PartExtractorResult per) {
                    per.isWeekDay = true;
                }
            });
        }
    },
    shortWeekdaysExtractor {
        private final Map<Locale, PartExtractor> extractors = new HashMap<>();
        @Override
        public PartExtractor get(Locale locale) {
            return extractors.computeIfAbsent(locale, l -> new EnumExtractor("shortWeekdaysExtractor", locale, "EEE") {
                @Override
                protected String[] getEnumValues(Locale locale) {
                    return new DateFormatSymbols(locale).getShortWeekdays();
                }
                @Override
                protected void visitPER(PartExtractorResult per) {
                    per.isWeekDay = true;
                }
            });
        }
    },
    yearWithEraExtractor {
        private final PartExtractor partExtractor = new YearWithEra();
        @Override
        public PartExtractor get(Locale locale) {
            return partExtractor;
        }
    },
    regularTimeExtractor {
        private final PartExtractor partExtractor = new RegularTimeExtractor();
        @Override
        public PartExtractor get(Locale locale) {
            return partExtractor;
        }
    },
    plainYearExtractor {
        private final PartExtractor partExtractor = new PartExtractorDelegate("plainYearExtractor",
                new RegexPartExtractor("plainYearExtractorWithMinus", ".*\\b?(-\\d{4})\\b?.*", "uuuu"),
                new RegexPartExtractor("plainYearExtractorWithoutMinus", ".*\\b?(\\d{4})\\b?.*", "yyyy")
        );
        @Override
        public PartExtractor get(Locale locale) {
            return partExtractor;
        }
    },
    monthNameExtractor {
        private final Map<Locale, PartExtractor> extractors = new HashMap<>();

        @Override
        public PartExtractor get(Locale locale) {
            return extractors.computeIfAbsent(locale, l -> new MonthNameExtractor(locale));
        }
    },
    monthDateExtractor {
        private final PartExtractor partExtractor = new MonthDateExtractor();
        @Override
        public PartExtractor get(Locale locale) {
            return partExtractor;
        }
    },
    eraAfterYearExtractor {
        private final PartExtractor partExtractor = new EraAfterYearExtractor();
        @Override
        public PartExtractor get(Locale locale) {
            return partExtractor;
        }
    },
    twoDigitYearExtractor {
        private final PartExtractor partExtractor = new PartExtractorDelegate("plainYearExtractor",
                new RegexPartExtractor("twoDigitYearExtractorWithMinus", ".*\\b?(-\\d{2})\\b?.*", "u"),
                new RegexPartExtractor("twoDigitYearExtractorWithoutMinus", ".*\\b?(\\d{2})\\b?.*", "y")
        );
        @Override
        public PartExtractor get(Locale locale) {
            return partExtractor;
        }
    },
//    fullMonthExtractor {
//        private final Map<Locale, PartExtractor> extractors = new HashMap<>();
//        @Override
//        public PartExtractor get(Locale locale) {
//            return extractors.computeIfAbsent(locale, l -> new FullMonthExtractor(locale));
//        }
//    },
//    shortMonthExtractor {
//        private final Map<Locale, PartExtractor> extractors = new HashMap<>();
//        @Override
//        public PartExtractor get(Locale locale) {
//            return extractors.computeIfAbsent(locale, l -> new ShortMonthExtractor(locale));
//        }
//    },
    /**
     * 2011-12-03
     *
     * ISO-8601 in practice is most used date time format.
     * While the standard allows separators to be optional (yes, you can write 20200101T010101Z),
     * the most common usage is with separators.
     * And the Java DateTimeFormatter implements only version with separators.
     * Another difference is that the 'T' separator is mandatory in Java.
     * ISO_DATE_TIME = 2011-12-03T10:15:30+01:00[Europe/Paris]
     *
     * there are some weirdo ISO formats I almost never seen in use, like
     * ISO_ORDINAL_DATE = 2012-337 (year-dayOfYear)
     * ISO_WEEK_DATE = 2012-W48-6 (year-week-weekDay)
     * BASIC_ISO_DATE = 20111203 (yyyyMMdd)
     *
     */
    allYMDPatternExtractor {
        private final PartExtractor partExtractor = new AllYMDPatternExtractor();
        @Override
        public PartExtractor get(Locale locale) {
            return partExtractor;
        }
    },

    ;

    public abstract PartExtractor get(Locale locale);

    //
//    /**
//     * [Mon, ]17 Sep 2019 12:34[:56] [+HHMM/GMT]
//     * weekDay is optional
//     * year only in 4 digits
//     * hour is 0-23 with leading zero
//     * minute is 0-59 with leading zero
//     * second is 0-59 with leading zero (optional)
//     * offset is +HHMM(or any other numbered form) or GMT (optional)
//     */
//    class RFC1123PatternExtractor extends RegexPartExtractor {
//        RFC1123PatternExtractor() {
//            super(".*\\b?(\\w{3}, \\d{1,2} \\w{3} \\d{4} \\d{2}:\\d{2}:\\d{2} [A-Z]{3}).*", "EEE, d MMM yyyy HH:mm:ss z");
//        }
//    }
//    PartExtractor rfc1123PatternExtractor = new RFC1123PatternExtractor();


}
