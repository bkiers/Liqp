package liqp.filters;

import liqp.TemplateContext;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * This filter requires <code>baseurl</code> parameter
 * that will be used as base for building relative url.
 */
public class Relative_Url extends Filter {
    public static final String root = "site";
    public static final String baseurl = "baseurl";
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        Object configRoot = context.get(root);

        if (configRoot instanceof Inspectable) {
            LiquidSupport evaluated = context.renderSettings.evaluate(context, (Inspectable) configRoot);
            configRoot = evaluated.toLiquid();
        }
        Object baseUrl = null;
        if (isMap(configRoot)) {
            baseUrl = asMap(configRoot).get(baseurl);
        }
        String valAsString = asString(value);

        // fast exit for valid absolute urls
        try {
            URI uri = new URI(valAsString);
            if (uri.getScheme() != null) {
                return valAsString;
            }
        } catch (URISyntaxException ignored) {
        }

        if (!valAsString.startsWith("/")) {
            valAsString = "/" + valAsString;
        }
        String baseUrlString = asString(baseUrl);
        if (baseUrlString.isEmpty()) {
            return valAsString;
        } else {
            if (!baseUrlString.startsWith("/")) {
                baseUrlString = "/" + baseUrlString;
            }
            String res;
            if ("/".equals(valAsString)) {
                if ("/".equals(baseUrlString)) {
                    res = "/";
                } else {
                    res = baseUrlString;
                }
            } else {
                res = baseUrlString + valAsString;
            }
            try {
                String query = null;
                String anchor = null;
                String path;
                String[] anchorParts = res.split("#", 2);
                if (anchorParts.length > 1) {
                    anchor = anchorParts[1];
                    res = anchorParts[0];
                }
                String[] parts = res.split("\\?", 2);
                if (parts.length > 1) {
                    path = parts[0];
                    query = parts[1];
                } else {
                    path = res;
                }
                URI uri = new URI(null,null, null, -1, path, query, anchor);
                String afterDecoding = uri.normalize().toASCIIString();
                if (afterDecoding.isEmpty()) {
                    afterDecoding = "/";
                }
                return afterDecoding;
            } catch (URISyntaxException e) {
                if (context.renderSettings.raiseExceptionsInStrictMode) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return res;
            }
        }
    }
}
