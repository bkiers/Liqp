package liqp.filters;

import org.jsoup.Jsoup;

class Strip_Newlines extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        return super.asString(value).replaceAll("[\r\n]++", "");
    }
}
