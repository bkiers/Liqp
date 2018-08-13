package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Url_DecodeTest {

    /*
        def test_url_decode
          assert_equal 'foo bar', @filters.url_decode('foo+bar')
          assert_equal 'foo bar', @filters.url_decode('foo%20bar')
          assert_equal 'foo+1@example.com', @filters.url_decode('foo%2B1%40example.com')
          assert_equal '1', @filters.url_decode(1)
          assert_equal '2001-02-03', @filters.url_decode(Date.new(2001, 2, 3))
          assert_nil @filters.url_decode(nil)
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 'foo+bar' | url_decode }}", "foo bar"},
                {"{{ 'foo%20bar' | url_decode }}", "foo bar"},
                {"{{ 'foo%2B1%40example.com' | url_decode }}", "foo+1@example.com"},
                {"{{ nil | url_decode }}", ""},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
