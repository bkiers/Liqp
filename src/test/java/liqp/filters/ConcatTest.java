package liqp.filters;

import liqp.Template;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConcatTest {

    /*
        def test_concat
          assert_equal [1, 2, 3, 4], @filters.concat([1, 2], [3, 4])
          assert_equal [1, 2, 'a'],  @filters.concat([1, 2], ['a'])
          assert_equal [1, 2, 10],   @filters.concat([1, 2], [10])
        end
    */
    @Test
    public void applyTest() {

        String[][] tests = {
                {"{{ a | concat: b }}", "1234", "{ \"a\": [1, 2], \"b\": [3, 4], \"c\": \"FOO\" }"},
                {"{{ a | concat: b }}", "12X", "{ \"a\": [1, 2], \"b\": [\"X\"], \"c\": \"FOO\" }"},
                {"{{ a | concat: b }}", "1210", "{ \"a\": [1, 2], \"b\": [10], \"c\": \"FOO\" }"},
                {"{{ c | concat: b }}", "34", "{ \"a\": [1, 2], \"b\": [3, 4], \"c\": \"FOO\" }"}
        };

        for (String[] test : tests) {

            Template template = Template.parse(test[0]);
            String rendered = String.valueOf(template.render(test[2]));

            assertThat(rendered, is(test[1]));
        }
    }

    @Test(expected = RuntimeException.class)
    public void applyTestParamNotArray() {
        Template template = Template.parse("{{ a | concat: c }}");
        template.render("{ \"a\": [1, 2], \"b\": [3, 4], \"c\": \"FOO\" }");
    }

    @Test(expected = RuntimeException.class)
    public void applyTestNoParam() {
        Template template = Template.parse("{{ a | concat }}");
        template.render("{ \"a\": [1, 2], \"b\": [3, 4], \"c\": \"FOO\" }");
    }
}
