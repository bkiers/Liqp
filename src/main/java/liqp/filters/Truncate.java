package liqp.filters;

import liqp.TemplateContext;

public class Truncate extends Filter {

    /*
     * truncate(input, length = 50, truncate_string = "...")
     *
     * Truncate a string down to x characters
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        if (value == null) {
            return "";
        }

        String text = super.asString(value, context);
        int length = 50;
        String truncateString = "...";

        if (params.length >= 1) {
            length = super.asNumber(super.get(0, params)).intValue();
        }

        if (params.length >= 2) {
            truncateString = super.asString(super.get(1, params), context);
        }

        // If the entire string fits untruncated, return the string.
        if (length >= text.length()) {
            return text;
        }

        // If the 'marker' takes up all the space, output the marker (even if
        // it's longer than the requested length).
        if (truncateString.length() >= length) {
            return truncateString;
        }

        // Otherwise, output as much text as will fit.
        int remainingChars = length - truncateString.length();

        return text.substring(0, remainingChars) + truncateString;
    }
}
