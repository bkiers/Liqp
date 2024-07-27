package liqp.filters;

import liqp.TemplateContext;

import java.util.regex.Pattern;

public class Strip_HTML extends Filter {

    // STRIP_HTML_BLOCKS = Regexp.union(
    //      /<script.*?<\/script>/m,
    //      /<!--.*?-->/m,
    //      /<style.*?<\/style>/m
    //    )
    private static final Pattern STRIP_HTML_BLOCKS = Pattern.compile("<script.*?</script>|<style.*?</style>|<!--.*?-->", Pattern.MULTILINE);

    // STRIP_HTML_TAGS = /<.*?>/m
    private static final Pattern STRIP_HTML_TAGS = Pattern.compile("<.*?>", Pattern.MULTILINE);

    /*
     * strip_html(input)
     *
     * Remove all HTML tags from the string
     */
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {

        String html = super.asString(value, context);
        html = STRIP_HTML_BLOCKS.matcher(html).replaceAll("");
        html = STRIP_HTML_TAGS.matcher(html).replaceAll("");

        return html;
    }
}
