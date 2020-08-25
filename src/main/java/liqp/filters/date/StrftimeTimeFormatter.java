package liqp.filters.date;

public class StrftimeTimeFormatter {
    final String flags;
    final int width;

    public final static StrftimeTimeFormatter DEFAULT_FORMATTER = new StrftimeTimeFormatter("", 0);

    public StrftimeTimeFormatter(String flags, int width) {
        this.flags = flags;
        this.width = width;
    }

    public int getWidth(int defaultWidth) {
        if (flags.indexOf('-') != -1) { // no padding
            return 0;
        }
        return this.width != 0 ? this.width : defaultWidth;
    }

    public char getPadder(char defaultPadder) {
        char padder = defaultPadder;
        for (int i = 0; i < flags.length(); i++) {
            switch (flags.charAt(i)) {
                case '_':
                    padder = ' ';
                    break;
                case '0':
                    padder = '0';
                    break;
                case '-': // no padding
                    padder = '\0';
                    break;
            }
        }
        return padder;
    }

    public String format(CharSequence sequence, long value, StrftimeDateFormatter.FieldType type) {
        int width = getWidth(type.defaultWidth);
        char padder = getPadder(type.defaultPadder);

        if (sequence == null) {
            sequence = formatNumber(value, width, padder);
        } else {
            sequence = padding(sequence, width, padder);
        }

        for (int i = 0; i < flags.length(); i++) {
            switch (flags.charAt(i)) {
                case '^':
                    sequence = sequence.toString().toUpperCase();
                    break;
                case '#': // change case
                    char last = sequence.charAt(sequence.length() - 1);
                    if (Character.isLowerCase(last)) {
                        sequence = sequence.toString().toUpperCase();
                    } else {
                        sequence = sequence.toString().toLowerCase();
                    }
                    break;
            }
        }

        return sequence.toString();
    }

    static CharSequence formatNumber(long value, int width, char padder) {
        if (value >= 0 || padder != '0') {
            return padding(Long.toString(value), width, padder);
        }
        return padding(new StringBuilder().append('-'), Long.toString(-value), width - 1, padder);
    }

    static StringBuilder formatSignedNumber(long value, int width, char padder) {
        StringBuilder out = new StringBuilder();
        if (padder == '0') {
            if (value >= 0) {
                return padding(out.append('+'), Long.toString(value), width - 1, padder);
            } else {
                return padding(out.append('-'), Long.toString(-value), width - 1, padder);
            }
        } else {
            if (value >= 0) {
                final StringBuilder str = new StringBuilder().append('+').append(Long.toString(value));
                return padding(out, str, width, padder);
            } else {
                return padding(out, Long.toString(value), width, padder);
            }
        }
    }

    private static final int SMALLBUF = 100;

    private static CharSequence padding(CharSequence sequence, int width, char padder) {
        final int len = sequence.length();
        if (len >= width) return sequence;

        if (width > SMALLBUF) throw new IndexOutOfBoundsException("padding width " + width + " too large");

        StringBuilder out = new StringBuilder(width + len);
        for (int i = len; i < width; i++) out.append(padder);
        out.append(sequence);
        return out;
    }

    private static StringBuilder padding(final StringBuilder out, CharSequence sequence,
                                         final int width, final char padder) {
        final int len = sequence.length();
        if (len >= width) return out.append(sequence);

        if (width > SMALLBUF) throw new IndexOutOfBoundsException("padding width " + width + " too large");

        out.ensureCapacity(width + len);
        for (int i = len; i < width; i++) out.append(padder);
        return out.append(sequence);
    }
}
