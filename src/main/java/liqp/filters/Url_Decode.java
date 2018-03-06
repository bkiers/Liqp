package liqp.filters;

import java.net.URLDecoder;

public class Url_Decode extends Filter {

    @Override
    public Object apply(Object value, Object... params) {

        try {
            return URLDecoder.decode(super.asString(value), "UTF-8");
        }
        catch (Exception e) {
            return value;
        }
    }
}
