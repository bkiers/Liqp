package liqp.filters;

import liqp.TemplateContext;

import java.net.URLDecoder;

public class Url_Decode extends Filter {

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {

        try {
            return URLDecoder.decode(super.asString(value, context), "UTF-8");
        }
        catch (Exception e) {
            return value;
        }
    }
}
