package liqp.filters;

import liqp.TemplateContext;
import liqp.TemplateParser;

import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class Absolute_Url extends Relative_Url {
    public static final String config = "config";
    public static final String url = "url";
    /*
     def compute_absolute_url(input)
        input = input.url if input.respond_to?(:url)
        return input if Addressable::URI.parse(input.to_s).absolute?

        site = @context.registers[:site]
        site_url = site.config["url"]
        return relative_url(input) if site_url.nil? || site_url == ""

        Addressable::URI.parse(
          site_url.to_s + relative_url(input)
        ).normalize.to_s
      end 
     */

    @Override
    public Object apply(TemplateContext context, Object value, Object... params) {
        String valAsString = asString(value, context);
        if (isValidAbsoluteUrl(valAsString)) {
            return valAsString;
        }
        Object configRoot = context.get(site);
        Map<String, Object> siteMap = objectToMap(configRoot, context);
        String baseUrl = asString(siteMap.get(baseurl), context);
        Object siteConfig = siteMap.get(config);
        Map<String, Object> configs = objectToMap(siteConfig, context);
        String siteUrl = asString(configs.get(url), context);
        String relativeUrl = getRelativeUrl(context, baseUrl, valAsString);
        if("".equals(siteUrl)) {
            return relativeUrl;
        } else {
            String res;
            if ((siteUrl != null && siteUrl.endsWith("/")) && "/".equals(relativeUrl)) {
                res = siteUrl;
            } else {
                res = siteUrl + relativeUrl;
            }
            try {
                // punicode java bug work around
                // IDN.toASCII not works if string start with scheme....
                res = convertUnicodeURLToAscii(res);
                if (valAsString.endsWith("/") && !res.endsWith("/")) {
                    res = res + "/";
                }
                return res;
            } catch (Exception e) {
                context.addError(e);
                if (context.getErrorMode() == TemplateParser.ErrorMode.STRICT) {
                    throw new RuntimeException(e);
                }
                return res;
            }
        }
    }

    public static String convertUnicodeURLToAscii(@SuppressWarnings("hiding") String url) throws URISyntaxException {
        if(url != null) {
            url = url.trim();
            URI uri = new URI(url);
            boolean includeScheme = true;

            // URI needs a scheme to work properly with authority parsing
            if(uri.getScheme() == null) {
                uri = new URI("http://" + url);
                includeScheme = false;
            }

            String scheme = uri.getScheme() != null ? uri.getScheme() + "://" : null;
            String authority = uri.getRawAuthority() != null ? uri.getRawAuthority() : ""; // includes domain and port
            String path = uri.getRawPath() != null ? uri.getRawPath() : "";
            String queryString = uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "";
            String fragment = uri.getRawFragment() != null ? "#" + uri.getRawFragment() : "";

            // Must convert domain to punycode separately from the path
            // see https://gist.github.com/msangel/f2224f72d386db3580ce18e5ef01bcc3
            url = (includeScheme ? scheme : "") + IDN.toASCII(authority) + path + queryString + fragment;

            // Convert path from unicode to ascii encoding
            url = new URI(url).normalize().toASCIIString();
        }
        return url;
    }
}
