package liqp.filters;

import liqp.Template;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SliceTest {

    /*
        def test_slice
          assert_equal 'oob', @filters.slice('foobar', 1, 3)
          assert_equal 'oobar', @filters.slice('foobar', 1, 1000)
          assert_equal '', @filters.slice('foobar', 1, 0)
          assert_equal 'o', @filters.slice('foobar', 1, 1)
          assert_equal 'bar', @filters.slice('foobar', 3, 3)
          assert_equal 'ar', @filters.slice('foobar', -2, 2)
          assert_equal 'ar', @filters.slice('foobar', -2, 1000)
          assert_equal 'r', @filters.slice('foobar', -1)
          assert_equal '', @filters.slice(nil, 0)
          assert_equal '', @filters.slice('foobar', 100, 10)
          assert_equal '', @filters.slice('foobar', -100, 10)
          assert_equal 'oob', @filters.slice('foobar', '1', '3')
          assert_raises(Liquid::ArgumentError) do
            @filters.slice('foobar', nil)
          end
          assert_raises(Liquid::ArgumentError) do
            @filters.slice('foobar', 0, "")
          end
        end
    */
    @Test
    public void applyTest() throws RecognitionException {

        String[][] tests = {
                {"", ""},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render());

            assertThat(rendered, is(test[1]));
        }
    }
}
