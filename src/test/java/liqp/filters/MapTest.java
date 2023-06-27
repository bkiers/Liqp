package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class MapTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{\"products\" : [\n" +
                "  {\"name\" : \"C\", \"price\" : 1}, \n" +
                "  {\"name\" : \"A\", \"price\" : 3},\n" +
                "  {\"name\" : \"B\", \"price\" : 2}\n" +
                "]}";

        String[][] tests = {
                {"{{ mu | map:'name' }}", ""},
                {"{{ products | map:'XYZ' }}", ""},
                {"{{ products | map:'XYZ' | sort | join }}", ""},
                {"{{ products | map:'name' | sort | join }}", "A B C"},
                {"{{ products | map:'price' | sort | join:'=' }}", "1=2=3"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_map
     *   assert_equal [1,2,3,4], @filters.map([{"a" => 1}, {"a" => 2}, {"a" => 3}, {"a" => 4}], 'a')
     *   assert_template_result 'abc', "{{ ary | map:'foo' | map:'bar' }}",
     *     'ary' => [{'foo' => {'bar' => 'a'}}, {'foo' => {'bar' => 'b'}}, {'foo' => {'bar' => 'c'}}]
     * end
     */
    @Test
    public void applyOriginalTest() {

        Filter filter = Filters.COMMON_FILTERS.get("map");

        Object[] rendered = (Object[]) filter.apply(
                new java.util.Map<?,?>[]{
                    Collections.singletonMap("a", 1),
                    Collections.singletonMap("a", 2),
                    Collections.singletonMap("a", 3),
                    Collections.singletonMap("a", 4),
                },
                new TemplateContext(),
                "a"
        );

        Object[] expected = {1, 2, 3, 4};

        assertThat(rendered, is(expected));

        final String json = "{\"ary\":[{\"foo\":{\"bar\":\"a\"}}, {\"foo\":{\"bar\":\"b\"}}, {\"foo\":{\"bar\":\"c\"}}]}";

        assertThat(TemplateParser.DEFAULT.parse("{{ ary | map:'foo' | map:'bar' }}").render(json), is("abc"));
    }
}
