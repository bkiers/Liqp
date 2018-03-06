package liqp.filters;

import liqp.Template;
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
        end
    */
    @Test
    public void applyTest() {

        String[][] tests = {
                {"{{ 'foobar' | slice: 1, 3 }}", "oob", "{}"},
                {"{{ 'foobar' | slice: 1, 1000 }}", "oobar", "{}"},
                {"{{ 'foobar' | slice: 1, 0 }}", "", "{}"},
                {"{{ 'foobar' | slice: 1, 1 }}", "o", "{}"},
                {"{{ 'foobar' | slice: 3, 3 }}", "bar", "{}"},
                {"{{ 'foobar' | slice: -2, 2 }}", "ar", "{}"},
                {"{{ 'foobar' | slice: -2, 1000 }}", "ar", "{}"},
                {"{{ 'foobar' | slice: -1 }}", "r", "{}"},
                {"{{ nil | slice: 0 }}", "", "{}"},
                {"{{ nil | slice: 5, 1000 }}", "", "{}"},
                {"{{ 'foobar' | slice: 100, 10 }}", "", "{}"},
                {"{{ 'foobar' | slice: 6 }}", "", "{}"},
                {"{{ 'foobar' | slice: -100, 10 }}", "", "{}"},
                {"{{ 'foobar' | slice: '1', '3' }}", "oob", "{}"},
                {"{{ x | slice: 1 }}", "2", "{ \"x\": [1, 2, 3, 4, 5] }"},
                {"{{ x | slice: 1, 3 }}", "234", "{ \"x\": [1, 2, 3, 4, 5] }"},
                {"{{ x | slice: 1, 3000 }}", "2345", "{ \"x\": [1, 2, 3, 4, 5] }"},
                {"{{ x | slice: -2, 2 }}", "45", "{ \"x\": [1, 2, 3, 4, 5] }"},
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }

    @Test(expected = RuntimeException.class)
    public void noParamsThrowsException() {
        Template.parse("{{ 'mu' | slice }}").render();
    }

    @Test(expected = RuntimeException.class)
    public void noIntegerParamThrowsException() {
        Template.parse("{{ 'mu' | slice: false }}").render();
    }

    @Test(expected = RuntimeException.class)
    public void noIntegersParamThrowsException() {
        Template.parse("{{ 'mu' | slice: 1, 3.1415 }}").render();
    }

    @Test(expected = RuntimeException.class)
    public void threeParamsThrowsException() {
        Template.parse("{{ 'mu' | slice: 1, 2, 3 }}").render();
    }
}
