package liqp.filters.date.fuzzy.extractors;

import java.util.regex.Matcher;

class RegularTimeExtractor extends RegexPartExtractor {

    public RegularTimeExtractor() {
        super("(?:^|.*?\\D)"
                + "("
                + "(?<hours>(\\d|0\\d|1\\d|2[0-3]))"
                + ":"
                + "(?<minutes>[0-5]\\d)"
                + "(?:"
                + ":(?<seconds>[0-5]\\d)"
                + "(?:\\.(?<milliseconds>\\d{1,9})?)?"
                + ")?" // end of seconds
                + "(?<ampm>\\s*[AaPp][Mm])?"
                + ")" // end of main group
                + "(?:$|\\D.*?)", null);
    }

    @Override
    public PartExtractorResult extract(String source) {
        Matcher m = pattern.matcher(source);
        if (m.matches()) {
            PartExtractorResult r = new PartExtractorResult();
            r.found = true;

            String ampmPart = "";
            String ampm = m.group("ampm");
            if (ampm != null) {
                ampmPart = ampm.substring(0, ampm.length() - 2) + "a";
            }

            boolean hasAmPm = !ampmPart.isEmpty();

            String hourPart;

            if (hasAmPm) {
                if (!m.group("hours").startsWith("0")) {
                    hourPart = "h"; // most often time with ampm is without leading zero
                } else {
                    hourPart = "hh"; // unless it explicitly has leading zero
                }
            } else {
                hourPart = m.group("hours").length() == 1 ? "H" : "HH";
            }

            r.start = m.start("hours");
            if (m.group("milliseconds") != null) {
                int millisecondsLength = m.group("milliseconds").length();
                r.end = m.end("milliseconds");
                r.formatterPattern =
                        hourPart + ":mm:ss." + repeat("S", millisecondsLength);
            } else if (m.group("seconds") != null) {
                r.end = m.end("seconds");
                r.formatterPattern = hourPart + ":mm:ss";
            } else if (m.group("minutes") != null) {
                r.end = m.end("minutes");
                r.formatterPattern = hourPart + ":mm";
            } else {
                r.end = m.end("hours");
                r.formatterPattern = hourPart;
            }
            if (hasAmPm) {
                r.formatterPattern += ampmPart;
                r.end = m.end("ampm");
            }
            return r;
        }
        return new PartExtractorResult();
    }

    static String repeat(String key, int count) {
        return new String(new char[count]).replace("\0", key);
    }
}
