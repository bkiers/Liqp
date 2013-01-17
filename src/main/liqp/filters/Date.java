package liqp.filters;

import java.text.SimpleDateFormat;

class Date extends Filter {

    private final static java.util.Map<Character, SimpleDateFormat> LIQUID_TO_JAVA_FORMAT =
            new java.util.HashMap<Character, SimpleDateFormat>();

    static {

        // %% - Literal ``%'' character
        LIQUID_TO_JAVA_FORMAT.put('%', new SimpleDateFormat("%"));

        // %a - The abbreviated weekday name (``Sun'')
        LIQUID_TO_JAVA_FORMAT.put('a', new SimpleDateFormat("EEE"));

        // %A - The  full  weekday  name (``Sunday'')
        LIQUID_TO_JAVA_FORMAT.put('A', new SimpleDateFormat("EEEE"));

        // %b - The abbreviated month name (``Jan'')
        LIQUID_TO_JAVA_FORMAT.put('b', new SimpleDateFormat("MMM"));
        LIQUID_TO_JAVA_FORMAT.put('h', new SimpleDateFormat("MMM"));

        // %B - The  full  month  name (``January'')
        LIQUID_TO_JAVA_FORMAT.put('B', new SimpleDateFormat("MMMM"));

        // %c - The preferred local date and time representation
        LIQUID_TO_JAVA_FORMAT.put('c', new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy"));

        // %d - Day of the month (01..31)
        LIQUID_TO_JAVA_FORMAT.put('d', new SimpleDateFormat("dd"));

        // %H - Hour of the day, 24-hour clock (00..23)
        LIQUID_TO_JAVA_FORMAT.put('H', new SimpleDateFormat("HH"));

        // %I - Hour of the day, 12-hour clock (01..12)
        LIQUID_TO_JAVA_FORMAT.put('I', new SimpleDateFormat("hh"));

        // %j - Day of the year (001..366)
        LIQUID_TO_JAVA_FORMAT.put('j', new SimpleDateFormat("DDD"));

        // %m - Month of the year (01..12)
        LIQUID_TO_JAVA_FORMAT.put('m', new SimpleDateFormat("MM"));

        // %M - Minute of the hour (00..59)
        LIQUID_TO_JAVA_FORMAT.put('M', new SimpleDateFormat("mm"));

        // %p - Meridian indicator (``AM''  or  ``PM'')
        LIQUID_TO_JAVA_FORMAT.put('p', new SimpleDateFormat("a"));

        // %S - Second of the minute (00..60)
        LIQUID_TO_JAVA_FORMAT.put('S', new SimpleDateFormat("ss"));

        // %U - Week  number  of the current year,
        //      starting with the first Sunday as the first
        //      day of the first week (00..53)
        LIQUID_TO_JAVA_FORMAT.put('U', new SimpleDateFormat("ww"));

        // %W - Week  number  of the current year,
        //      starting with the first Monday as the first
        //      day of the first week (00..53)
        LIQUID_TO_JAVA_FORMAT.put('W', new SimpleDateFormat("ww"));

        // %w - Day of the week (Sunday is 0, 0..6)
        LIQUID_TO_JAVA_FORMAT.put('w', new SimpleDateFormat("F"));

        // %x - Preferred representation for the date alone, no time
        LIQUID_TO_JAVA_FORMAT.put('x', new SimpleDateFormat("MM/dd/yy"));

        // %X - Preferred representation for the time alone, no date
        LIQUID_TO_JAVA_FORMAT.put('X', new SimpleDateFormat("HH:mm:ss"));

        // %y - Year without a century (00..99)
        LIQUID_TO_JAVA_FORMAT.put('y', new SimpleDateFormat("yy"));

        // %Y - Year with century
        LIQUID_TO_JAVA_FORMAT.put('Y', new SimpleDateFormat("yyyy"));

        // %Z - Time zone name
        LIQUID_TO_JAVA_FORMAT.put('Z', new SimpleDateFormat("z"));
    }

    /*
     * (Object) date(input, format)
     *
     * Reformat a date
     *
     * %a - The abbreviated weekday name (``Sun'')
     * %A - The  full  weekday  name (``Sunday'')
     * %b - The abbreviated month name (``Jan'')
     * %B - The  full  month  name (``January'')
     * %c - The preferred local date and time representation
     * %d - Day of the month (01..31)
     * %H - Hour of the day, 24-hour clock (00..23)
     * %I - Hour of the day, 12-hour clock (01..12)
     * %j - Day of the year (001..366)
     * %m - Month of the year (01..12)
     * %M - Minute of the hour (00..59)
     * %p - Meridian indicator (``AM''  or  ``PM'')
     * %S - Second of the minute (00..60)
     * %U - Week  number  of the current year,
     *      starting with the first Sunday as the first
     *      day of the first week (00..53)
     * %W - Week  number  of the current year,
     *      starting with the first Monday as the first
     *      day of the first week (00..53)
     * %w - Day of the week (Sunday is 0, 0..6)
     * %x - Preferred representation for the date alone, no time
     * %X - Preferred representation for the time alone, no date
     * %y - Year without a century (00..99)
     * %Y - Year with century
     * %Z - Time zone name
     * %% - Literal ``%'' character
     */
    @Override
    public Object apply(Object value, Object... params) {

        try {
            final long seconds = super.asString(value).equals("now") ?
                    System.currentTimeMillis() / 1000L :
                    Long.valueOf(super.asString(value));
            final java.util.Date date = new java.util.Date(seconds * 1000L);
            final String format = super.asString(super.get(0, params));
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(date);

            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < format.length(); i++) {

                char ch = format.charAt(i);

                if(ch == '%') {

                    i++;

                    if(i == format.length()) {
                        // a trailing (single) '%' sign: just append it
                        builder.append("%");
                        break;
                    }

                    char next = format.charAt(i);

                    SimpleDateFormat javaFormat = LIQUID_TO_JAVA_FORMAT.get(next);

                    if(javaFormat == null) {
                        // no valid date-format: append the '%' and the 'next'-char
                        builder.append("%").append(next);
                    }
                    else {
                        builder.append(javaFormat.format(date));
                    }
                }
                else {
                    builder.append(ch);
                }
            }

            return builder.toString();
        }
        catch (Exception e) {
            return value;
        }
    }
}
