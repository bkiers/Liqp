package liqp.filters;

import liqp.TemplateContext;
import org.jsoup.Jsoup;

public class Strip_HTML extends Filter {

    /*
     * strip_html(input)
     *
     * Remove all HTML tags from the string
     */
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        String html = super.asString(value, context);

        return Jsoup.parse(html).text();
    }
}
