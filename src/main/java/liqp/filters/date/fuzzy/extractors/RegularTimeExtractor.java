package liqp.filters.date.fuzzy.extractors;

import java.util.List;
import java.util.regex.Matcher;
import liqp.filters.date.fuzzy.Part;

class RegularTimeExtractor extends RegexPartExtractor {

    public RegularTimeExtractor() {
        super("RegularTimeExtractor", "(?:^|.*?\\D)"
                + "("
                + "(?<hours>(\\d|0\\d|1\\d|2[0-3]))"
                + ":"
                + "(?<minutes>[0-5]\\d)"
                + "(?:"
                + ":(?<seconds>[0-5]\\d)"
                + "(?:\\.(?<milliseconds>\\d{1,9})?)?"
                + ")?" // end of seconds
                + "(?<ampm>\\s*[AaPp][Mm](?![A-Za-z]))?"
                + ")" // end of main group
                + "(?:$|\\D.*?)", null);

        // https://www.ibm.com/docs/en/cloud-pak-system-w4600/2.3.3?topic=SSDLT6_2.3.3/psapsys_restapi/time_zone_list.htm
    }

    @Override
    public PartExtractorResult extract(String source, List<Part> parts, int i) {
        Matcher m = pattern.matcher(source);
        if (m.matches()) {
            PartExtractorResult r = new PartExtractorResult(name);
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
            String resPattern;
            if (m.group("milliseconds") != null) {
                int millisecondsLength = m.group("milliseconds").length();
                r.end = m.end("milliseconds");
                resPattern = 
                        hourPart + ":mm:ss." + repeat("S", millisecondsLength);
            } else if (m.group("seconds") != null) {
                r.end = m.end("seconds");
                resPattern = hourPart + ":mm:ss";
            } else if (m.group("minutes") != null) {
                r.end = m.end("minutes");
                resPattern = hourPart + ":mm";
            } else {
                r.end = m.end("hours");
                resPattern = hourPart;
            }
            if (hasAmPm) {
                resPattern += ampmPart;
                r.end = m.end("ampm");
            }
            r.formatterPatterns = newList(resPattern);
            return r;
        }
        return new PartExtractorResult(name);
    }

    static String repeat(String key, int count) {
        return new String(new char[count]).replace("\0", key);
    }
}
