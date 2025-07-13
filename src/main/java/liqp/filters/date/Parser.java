package liqp.filters.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.util.*;

import static java.time.temporal.ChronoField.*;

public class Parser extends BasicDateParser {

    /**
     * In case if anyone interesting about full set
     * of supported by ruby date patterns:
     * there no such set as the parsing there happening based on
     * heuristic algorithms.
     * This is how it looks like(~3K lines just for date parse):
     * https://github.com/ruby/ruby/blob/ee102de6d7ec2454dc5da223483737478eb7bcc7/ext/date/date_parse.c
     *
     * And here's python.
     * Just an example how it is violating standard in details regarding timezone representation:
     * https://docs.python.org/3/library/datetime.html#strftime-and-strptime-behavior
     */
    public static List<String> datePatterns = new ArrayList<>();

    static {

        datePatterns.add("EEE MMM d hh:mm:ss yyyy");
        datePatterns.add("EEE MMM d hh:mm yyyy");
        datePatterns.add("yyyy-M-d");
        datePatterns.add("d-M-yyyy");
        datePatterns.add("d-M-yy");
        datePatterns.add("yy-M-d");

        datePatterns.add("d/M/yyyy");
        datePatterns.add("yyyy/M/d");
        datePatterns.add("d/M/yy");
        datePatterns.add("yy/M/d");
        datePatterns.add("M/yyyy");
        datePatterns.add("yyyy/M");
        datePatterns.add("M/d");
        datePatterns.add("d/M");

        // this is section without `T`, change here and do same change in section below with `T`
        datePatterns.add("yyyy-M-d HH:mm");
        datePatterns.add("yyyy-M-d HH:mm X");
        datePatterns.add("yyyy-M-d HH:mm Z");
        datePatterns.add("yyyy-M-d HH:mm z");
        datePatterns.add("yyyy-M-d HH:mm'Z'");

        datePatterns.add("yyyy-M-d HH:mm:ss");
        datePatterns.add("yyyy-M-d HH:mm:ss X");
        datePatterns.add("yyyy-M-d HH:mm:ss Z");
        datePatterns.add("yyyy-M-d HH:mm:ss z");
        datePatterns.add("yyyy-M-d HH:mm:ss'Z'");

        datePatterns.add("yyyy-M-d HH:mm:ss.SSS");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSS X");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSS Z");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSS z");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSS'Z'");

        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSS");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSS X");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSS Z");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSS z");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSS'Z'");

        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSSSSS");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSSSSS X");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSSSSS Z");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSSSSS z");
        datePatterns.add("yyyy-M-d HH:mm:ss.SSSSSSSSS'Z'");

        // this is section with `T`
        datePatterns.add("yyyy-M-d'T'HH:mm");
        datePatterns.add("yyyy-M-d'T'HH:mm X");
        datePatterns.add("yyyy-M-d'T'HH:mm Z");
        datePatterns.add("yyyy-M-d'T'HH:mm z");
        datePatterns.add("yyyy-M-d'T'HH:mm'Z'");

        datePatterns.add("yyyy-M-d'T'HH:mm:ss");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss X");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss Z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss'Z'");

        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSS");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSS X");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSS Z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSS z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSS'Z'");

        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSS");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSS X");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSS Z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSS z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSS'Z'");

        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSSSSS");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSSSSS X");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSSSSS Z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSSSSS z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ss.SSSSSSSSS'Z'");

        datePatterns.add("EEE MMM d HH:mm:ss yyyy");
        datePatterns.add("EEE, d MMM yyyy HH:mm:ss Z");
        datePatterns.add("EEE, d MMM yyyy HH:mm:ss z");
        datePatterns.add("MMM d HH:mm:ss yyyy");
        datePatterns.add("d MMM yyyy HH:mm:ss Z");
        datePatterns.add("d MMM yyyy HH:mm:ss z");
        datePatterns.add("yyyy-M-d'T'HH:mm:ssXXX");

        datePatterns.add("d MMM");
        datePatterns.add("d MMM yy");
        datePatterns.add("d MMM yyyy");
        datePatterns.add("d MMMM");
        datePatterns.add("d MMMM yy");
        datePatterns.add("d MMMM yyyy");

        datePatterns.add("MMM d");
        datePatterns.add("MMM d, yy");
        datePatterns.add("MMM d, yyyy");

        datePatterns.add("MMMM d");
        datePatterns.add("MMMM d, yy");
        datePatterns.add("MMMM d, yyyy");

        datePatterns.add("MMM");
        datePatterns.add("MMM yy");
        datePatterns.add("MMM yyyy");

        datePatterns.add("MMMM");
        datePatterns.add("MMMM yy");
        datePatterns.add("MMMM yyyy");

        datePatterns.add("H:mm");
        datePatterns.add("H:mm:ss");
    }


    public Parser() {
        super(datePatterns);
    }

    public ZonedDateTime parse(String str, Locale locale, ZoneId defaultZone) {
        String normalized = str.toLowerCase();
        normalized = removeSequentialSuffixes(normalized);
        return parseUsingCachedPatterns(normalized, locale, defaultZone);
    }

}
