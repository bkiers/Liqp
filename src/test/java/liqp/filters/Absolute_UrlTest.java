package liqp.filters;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import liqp.Template;
import liqp.TemplateParser;

public class Absolute_UrlTest {

    // site.baseurl
    // site.config.url
    private java.util.Map<String, Object> getData(Object siteUrl, String baseurl) {
        Map<String, Object> siteMap = new HashMap<>();
        siteMap.put("baseurl", baseurl);
        Map<String, Object> config = Collections.singletonMap("url", siteUrl);
        siteMap.put("config", config);
        return Collections.singletonMap("site", (Object)siteMap);
    }
    
    /*
      should "produce an absolute URL from a page URL" do
        page_url = "/about/my_favorite_page/"
        assert_equal "http://example.com/base#{page_url}", @filter.absolute_url(page_url)
      end
     */
    @Test
    public void testProduceAnAbsoluteURLFromAPageURL() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ '/about/my_favorite_page/' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", "base");
        
        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/base/about/my_favorite_page/", res);
    }


    /*
      should "ensure the leading slash" do
        page_url = "about/my_favorite_page/"
        assert_equal "http://example.com/base/#{page_url}", @filter.absolute_url(page_url)
      end
     */
    @Test
    public void testEnsureTheLeadingSlash() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ 'about/my_favorite_page/' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", "/base");

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/base/about/my_favorite_page/", res);
    }

    /*
      should "ensure the leading slash for the baseurl" do
        page_url = "about/my_favorite_page/"
        filter = make_filter_mock(
          "url"     => "http://example.com",
          "baseurl" => "base"
        )
        assert_equal "http://example.com/base/#{page_url}", filter.absolute_url(page_url)
      end
     */
    @Test
    public void testEnsureTheLeadingSlashForTheBaseurl() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ 'about/my_favorite_page/' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", "base");

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/base/about/my_favorite_page/", res);
    }


    /*
     should "be ok with a blank but present 'url'" do
        page_url = "about/my_favorite_page/"
        filter = make_filter_mock(
          "url"     => "",
          "baseurl" => "base"
        )
        assert_equal "/base/#{page_url}", filter.absolute_url(page_url)
      end
    */
    @Test
    public void testBeOkWithBlankButPresentURL() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ 'about/my_favorite_page/' | absolute_url }}");
        Map<String,Object> data = getData("", "base");

        // when
        String res = template.render(data);

        // then
        assertEquals("/base/about/my_favorite_page/", res);
    }

    /*
     should "be ok with a nil 'url'" do
        page_url = "about/my_favorite_page/"
        filter = make_filter_mock(
          "url"     => nil,
          "baseurl" => "base"
        )
        assert_equal "/base/#{page_url}", filter.absolute_url(page_url)
      end
    */
    @Test
    public void testBeOkWithANilURL() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ 'about/my_favorite_page/' | absolute_url }}");
        Map<String,Object> data = getData(null, "base");

        // when
        String res = template.render(data);

        // then
        assertEquals("/base/about/my_favorite_page/", res);
    }

    /*
     should "be ok with a nil 'baseurl'" do
        page_url = "about/my_favorite_page/"
        filter = make_filter_mock(
          "url"     => "http://example.com",
          "baseurl" => nil
        )
        assert_equal "http://example.com/#{page_url}", filter.absolute_url(page_url)
      end
    */
    @Test
    public void testBeOkWithANilBaseurl() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ 'about/my_favorite_page/' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", null);

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/about/my_favorite_page/", res);
    }

    /*
     should "not prepend a forward slash if input is empty" do
        page_url = ""
        filter = make_filter_mock(
          "url"     => "http://example.com",
          "baseurl" => "/base"
        )
        assert_equal "http://example.com/base", filter.absolute_url(page_url)
      end
    */
    @Test
    public void testNotPrependForwardSlashIfInputIsEmpty() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ '' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", "/base");

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/base", res);
    }

    /*
     should "not append a forward slash if input is '/'" do
        page_url = "/"
        filter = make_filter_mock(
          "url"     => "http://example.com",
          "baseurl" => "/base"
        )
        assert_equal "http://example.com/base/", filter.absolute_url(page_url)
      end
    */
    @Test
    public void testNotAppendForwardSlashIfInputIsSlash() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ '/' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", "/base");

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/base/", res);
    }

    /*
     should "not append a forward slash if input is '/' and nil 'baseurl'" do
        page_url = "/"
        filter = make_filter_mock(
          "url"     => "http://example.com",
          "baseurl" => nil
        )
        assert_equal "http://example.com/", filter.absolute_url(page_url)
      end
    */
    @Test
    public void testNotAppendForwardSlashIfInputIsSlashAndNilBaseurl() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ '/' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", null);

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/", res);
    }

    /*
     should "not append a forward slash if both input and baseurl are simply '/'" do
        page_url = "/"
        filter = make_filter_mock(
          "url"     => "http://example.com",
          "baseurl" => "/"
        )
        assert_equal "http://example.com/", filter.absolute_url(page_url)
      end
    */
    @Test
    public void notAppendForwardSlashIfBothInputAndBaseurlAreAimplySlash() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ '/' | absolute_url }}");
        Map<String,Object> data = getData("http://example.com", "/");

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/", res);
    }

    /*
     should "normalize international URLs" do
        page_url = ""
        filter = make_filter_mock(
          "url"     => "http://ümlaut.example.org/",
          "baseurl" => nil
        )
        assert_equal "http://xn--mlaut-jva.example.org/", filter.absolute_url(page_url)
      end
    */
    @Test
    public void shouldNormalizeInternationalURLs() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ '' | absolute_url }}");
        Map<String,Object> data = getData("http://ümlaut.example.org/", null);

        // when
        String res = template.render(data);

        // then
        assertEquals("http://xn--mlaut-jva.example.org/", res);
    }

    @Test
    public void shouldNormalizeInternationalURLsInPath() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ 'ü' | absolute_url }}");
        Map<String,Object> data = getData("http://ümlaut.example.org/", null);

        // when
        String res = template.render(data);

        // then
        assertEquals("http://xn--mlaut-jva.example.org/%C3%BC", res);
    }

    /*
     should "not modify an absolute URL" do
        page_url = "http://example.com/"
        assert_equal "http://example.com/", @filter.absolute_url(page_url)
      end
    */
    @Test
    public void shouldNotModifyAbsoluteURL() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ 'http://example.com/' | absolute_url }}");
        Map<String,Object> data = getData("http://ümlaut.example.org/", null);

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.com/", res);
    }

    /*
      should "transform the input URL to a string" do
        page_url = "/my-page.html"
        filter = make_filter_mock("url" => Value.new(proc { "http://example.org" }))
        assert_equal "http://example.org#{page_url}", filter.absolute_url(page_url)
      end
    */
    @Test
    public void shouldTransformInputURLToString() {
        // given
        Template template = TemplateParser.DEFAULT.parse("{{ '/my-page.html' | absolute_url }}");
        Map<String,Object> data = getData(new Object() {
            @Override
            public String toString() {
                return "http://example.org";
            }
        }, null);

        // when
        String res = template.render(data);

        // then
        assertEquals("http://example.org/my-page.html", res);
    }
}