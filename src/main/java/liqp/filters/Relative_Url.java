package liqp.filters;

import liqp.TemplateContext;
import liqp.TemplateParser;
import liqp.parser.Inspectable;
import liqp.parser.LiquidSupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

/**
 * This filter requires <code>baseurl</code> parameter
 * that will be used as base for building relative url.
 */
public class Relative_Url extends Filter {
    public static final String site = "site";
    public static final String baseurl = "baseurl";
    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {
        String valAsString = asString(value, context);

        // fast exit for valid absolute urls
        if (isValidAbsoluteUrl(valAsString)) {
            return valAsString;
        }
        Map<String, Object> siteMap = objectToMap(context.get(site), context);
        String baseUrl = asString(siteMap.get(baseurl), context);
        return getRelativeUrl(context, baseUrl, valAsString);
    }

    protected String getRelativeUrl(TemplateContext context, String baseUrl, String valAsString) {
        
        if (!valAsString.startsWith("/")) {
            valAsString = "/" + valAsString;
        }
        String baseUrlString = asString(baseUrl, context);
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
                context.addError(e);
                if (context.getErrorMode() == TemplateParser.ErrorMode.STRICT) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return res;
            }
        }
    }

    protected Map<String, Object> objectToMap(Object configRoot, TemplateContext context) {
        if (configRoot instanceof Inspectable) {
            LiquidSupport evaluated = context.getParser().evaluate(configRoot);
            configRoot = evaluated.toLiquid();
        }
        Map<String, Object> siteMap;
        if (isMap(configRoot)) {
            siteMap = asMap(configRoot);
        } else {
            siteMap = Collections.emptyMap();
        }
        return siteMap;
    }

    protected boolean isValidAbsoluteUrl(String valAsString) {
        try {
            URI uri = new URI(valAsString);
            if (uri.getScheme() != null) {
                return true;
            }
        } catch (URISyntaxException ignored) {
        }
        return false;
    }
}
