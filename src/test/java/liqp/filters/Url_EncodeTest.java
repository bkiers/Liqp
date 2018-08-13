package liqp.filters;

import liqp.Template;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Url_EncodeTest {

    /*
        def test_url_encode
          assert_equal 'foo%2B1%40example.com', @filters.url_encode('foo+1@example.com')
          assert_equal '1', @filters.url_encode(1)
          assert_equal '2001-02-03', @filters.url_encode(Date.new(2001, 2, 3))
          assert_nil @filters.url_encode(nil)
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"{{ 'foo+1@example.com' | url_encode }}", "foo%2B1%40example.com"},
                {"{{ '1' | url_encode }}", "1"},
                {"{{ nil | url_encode }}", ""},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
