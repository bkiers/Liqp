package liqp.filters;

import org.jsoup.Jsoup;

class Strip_HTML extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        String html = super.asString(value);

        return Jsoup.parse(html).text();
    }
}
