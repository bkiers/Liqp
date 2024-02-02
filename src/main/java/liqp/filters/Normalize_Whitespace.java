package liqp.filters;

import liqp.TemplateContext;

import java.util.regex.Pattern;

public class Normalize_Whitespace extends Filter {

    private static Pattern pattern = Pattern.compile("\\s+");

    public Normalize_Whitespace() {
        super("normalize_whitespace");
    }

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {
        if (value == null) {
            return "";
        }
        String string = value.toString().trim();

        return pattern.matcher(string).replaceAll(" ");
    }
}
